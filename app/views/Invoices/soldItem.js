
Number.prototype.toMoney = function(decimals, decimal_sep, thousands_sep)
{
    var n = this;
    //if decimal is zero we must take it, it means user does not want to show any decimal
    var c = isNaN(decimals) ? 2 : Math.abs(decimals);
    //if no decimal separator is passed we use the comma as default decimal separator (we MUST use a decimal separator)
    var d = decimal_sep || Number("1.2").toLocaleString().substr(1,1);
    /*
     * according to [http://stackoverflow.com/questions/411352/how-best-to-determine-if-an-argument-is-not-sent-to-the-javascript-function]
     * the fastest way to check for not defined parameter is to use typeof value === 'undefined'
     * rather than doing value === undefined.
     */
    //if you don't want to use a thousands separator you can pass empty string as thousands_sep value
    var t = (typeof thousands_sep === 'undefined') ? Number("1000").toLocaleString().substr(1,1) : thousands_sep;
    var sign = (n < 0) ? '-' : '';
    //extracting the absolute value of the integer part of the number and converting to string
    var i = parseInt(n = Math.abs(n).toFixed(c)) + '';
    var j = ((j = i.length) > 3) ? j % 3 : 0;
    return sign + (j ? i.substr(0, j) + t : '') + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : '');
}

var currentLine = null;
var suggestedNames = ko.observableArray([]);

var item  = function (data) {
    ko.mapping.fromJS(data, {}, this);

    this.name.subscribe(function () {
        currentLine = this;
        suggestedNames([]);
        var url = #{jsAction @Invoices.getCompletions() /};
        var data = {startsWith: this.name(), maxRows: 12};
        $.post(url(), data, function(jsonResult) {
            if(jsonResult) {
                console.log("jsonResult: ", jsonResult);
                var mappedItems = $.map(jsonResult, function(el) {
                    return {
                        label: el.name + " - " + el.description,
                        value: el
                    };
                });
                suggestedNames(mappedItems);
            }
        });
    }.bind(this));

    this.nettVal = ko.dependentObservable(function () {
        var retval = parseFloat(this.price()) * parseFloat(this.quantity()) * (1 - parseFloat(this.rebate())/100);
        return retval.toMoney();
    }, this);

    // for further calculations without locale format
    this._grossVal = ko.dependentObservable(function (){
        var retval = parseFloat(this.nettVal()) * (1 + parseFloat(this.vatRate())/100);
        return retval;
    }, this);
    // value for presentation
    this.grossVal = ko.dependentObservable(function (){
        return this._grossVal().toMoney();
    }, this);
}

var emptyItem = {
    name: '',
    description: '',
    price: 0,
    rebate: 0,
    quantity: 1,
    vatRate: 0,
    notes: ''
};

var viewModel = function() {
    this.items = ko.observableArray([ new item(emptyItem) ]);

    this.addItem = function() {
        this.items.push(new item(emptyItem));
    };

    this.deleteItem = function(anitem) {
        this.items.remove(anitem);
    };

    this.total = ko.dependentObservable( function() {
        var sum = 0;
        for(var i = 0; i < this.items().length; i++) {
            sum += parseFloat(this.items()[i]._grossVal());
        }
        return sum.toMoney();
    }, this);
};

var fields_values = null;
ko.bindingHandlers.autocomplete = {
    update: function(element, list, allBindings) {
        $(element).autocomplete({
            focus: function(event, ui) {
                fields_values = ui.item.value;
                currentLine.name(fields_values['name']);
                return false;
            },
            source: list(),
            select: function(event, ui) {
                fields_values = ui.item.value;
                currentLine.name(fields_values['name']);
                return false;
            },
            close: function(event, ui) {
                if(fields_values != null) {
                    ko.mapping.updateFromJS(currentLine, fields_values);
                    fields_values = null;
                } else {
                    console.log("fields_values are null");
                }
            }
        });
    }
};

$(function() {
    ko.applyBindings(viewModel(), document.getElementById("itemsTable"));
});
