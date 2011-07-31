;(function($) {
    var app = $.sammy('#invoiceItemsTable', function() {
        this.debug = true;
        this.raise_errors = true;
        var fields_values = null;

        this.swap = function(content) {
            this.$element().hide('slow').html(content).show('slow');
        }

        this.get('#/', function(context) {
            context.log('main');
            sammy = this;
            $('#itemName').autocomplete({
                source: function (request, response) {
                    Sammy.log("autocomplete::source req:" + request);
                    var url = #{jsAction @Invoices.getCompletions() /};
                    var data = {
                        startsWith: request.term,
                        maxRows: 12
                    };
                    return context.send($.post, url(), data)
                        .then(function(contents) {
                            context.log("autocomplete contents: " + contents);
                            if (contents.length == 0) {
                                response([{label: "&{'inventory.item.unknown'}", value: null}]);
                            } else {
                                response($.map(contents, function(item) {
                                    return {
                                        label: item.name + " - " + item.description,
                                        value: item
                                    };
                                }));
                            }
                        });
                },

                minLength: 0,

                select: function (event, ui) {
                    fields_values = ui.item.value;
                    //event.stopPropagation();
                    //return false; // this cancels the default event action
                },

                open: function () {
                    Sammy.log("autocomplete::open");
                },

                close: function () {
                    Sammy.log("autocomplete::close");
                    if (fields_values != null) {
                        // TODO Handle the non-existing entry case
                        Sammy.log("autocomplete::close field_values: " + fields_values.toSource());

                        $("input[name='item.item.name']").val(fields_values['name']);
                        $("textarea[name='item.item.description']").val(fields_values['description']);
                        $("input[name='item.retailPrice']").val(fields_values['price']);
                        $("input[name='item.rebate']").val("0");
                        $("input[name='item.quantity']").val("1");
                        $("input[name='item.vatRate']").val(fields_values['vatRate']);
                        $(".autocompleteFix").each(function() {
                            Sammy.log("Triggering KO's init event for: " + this);
                            ko.utils.triggerEvent(this, 'init');
                        });
                        fields_values = null;
                    } else {
                        Sammy.log("fields_values are null");
                    }
                }
            });
        });
    });


    /*$(function( ) {
        app.run('#/');
    });*/
})(jQuery);

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

var suggestedNames = ko.observableArray([]);
var currentLine = null;

var item  = function () {
    this.name = ko.observable('');
    this.name.subscribe(function () {
        console.log("+++completion subscriber");
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
        console.log("---completion subscriber");
    }.bind(this));
    this.description = ko.observable('');
    this.price = ko.observable(0);
    this.rebate = ko.observable(0);
    this.quantity = ko.observable(1);
    this.vatRate = ko.observable(0);
    this.notes = ko.observable('');
    this.nettVal = ko.dependentObservable(function () {
        var retval = parseFloat(this.price()) * parseFloat(this.quantity()) * (1 - parseFloat(this.rebate())/100);
        //Sammy.log("KO:itemNettVal recalculation: " + retval);
        return retval.toMoney();
    }, this);

    this.grossVal = ko.dependentObservable(function (){
        var retval = parseFloat(this.nettVal()) * (1 + parseFloat(this.vatRate())/100);
        //Sammy.log("KO:itemTotVal recalculation: " + retval + " vat factor: " + (1 + parseFloat(this.vatRate())/100));
        return retval.toMoney();
    }, this);

}

var viewModel = {
    items: ko.observableArray([ new item() ]),
    addItem: function() {
        this.items.push(new item());
    },
    deleteItem: function(anitem) {
        this.items.remove(anitem);
    }
};

var fields_values = null;
ko.bindingHandlers.autocomplete = {
    update: function(element, list, allBindings) {
        // console.log("+++autocompletion hook: element " + element.name + ", list: " + list() + ", allBindings().autocompleteText: " + allBindings().autocompleteText);
        $(element).autocomplete({
            focus: function(event, ui) {
                console.log("autocomplete::focus");
                fields_values = ui.item.value;
                currentLine.name(fields_values['name']);
                return false;
            },
            source: list(),
            select: function(event, ui) {
                console.log("autocomplete::select");
                fields_values = ui.item.value;
                currentLine.name(fields_values['name']);
                return false;
            },
            close: function(event, ui) {
                console.log("autocomplete::close");
                if(fields_values != null) {
                    //currentLine.name(fields_values['name']);
                    //currentLine.description(fields_values['description']);
                    ko.mapping.updateFromJS(currentLine, fields_values);
                    fields_values = null;
                } else {
                    console.log("fields_values are null");
                }
            }
        });
        // console.log("---autocompletion hook");
        //$(element).autocomplete().trigger("setOptions", { data:  });
    }
};

$( function () {
    ko.applyBindings(viewModel, document.getElementById("itemsTable"));
});
