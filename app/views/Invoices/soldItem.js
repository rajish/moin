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

var item  = function () {
    this.name = ko.observable('');
    this.name.subscribe(function () {
        console.log("+++completion subscriber");
        var self = this;
        suggestedNames([]);
        var url = #{jsAction @Invoices.getCompletions() /};
        var data = {startsWith: self.name(), maxRows: 12};
        //setTimeout(function () {
        $.post(url(), data, function(jsonResult) {
            if(jsonResult) {
                console.log("jsonResult: ", jsonResult);
                var mappedItems = $.map(jsonResult, function(el) {
                    return {
                        name: el.name + " - " + el.description,
                        value: el
                    };
                });
                suggestedNames(mappedItems);
            }
        });
        //}.bind(this), 1000);
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


ko.bindingHandlers.autocomplete = {
    update: function(element, list, allBindings) {
        console.log("+++autocompletion hook: element " + element.name + ", list: " + list + ", allBindings().autocompleteText: " + allBindings().autocompleteText);
        var searchFor = allBindings().autocompleteText;
        var completionValues = ko.utils.arrayMap(list(), function (item) {
            return item[searchFor];
        });
        $(element).autocomplete({
            source: completionValues,
            create: function(event, ui) {
                console.log("autocomplete::create");
            },
            search: function(event, ui) {
                console.log("autocomplete::search");
            },
            open: function(event, ui) {
                console.log("autocomplete::open");
            },
            focus: function(event, ui) {
                console.log("autocomplete::focus");
            },
            select: function(event, ui) {
                console.log("autocomplete::select");
            },
            close: function(event, ui) {
                console.log("autocomplete::close");
            },
            change: function(event, ui) {
                console.log("autocomplete::change");
            }
        });
        console.log("---autocompletion hook");
        //$(element).autocomplete().trigger("setOptions", { data: ko.utils.unwrapObservable(list) });
    }
};

//jqAuto -- additional options to pass to autocomplete
//jqAutoSource -- the array of choices
//jqAutoValue -- where to write the selected value
//jqAutoSourceLabel -- the property name that should be displayed in the possible choices
//jqAutoSourceValue -- the property name to use for the value
ko.bindingHandlers.jqAuto = {
    init: function(element, valueAccessor, allBindingsAccessor, viewModel) {
        console.log("jqAuto::init");
        var options = valueAccessor() || {};
        var allBindings = allBindingsAccessor();
        var unwrap = ko.utils.unwrapObservable;

        //handle value changing
        var modelValue = allBindings.jqAutoValue;
        if (modelValue) {
            var handleValueChange = function(event, ui) {
                var valueToWrite = ui.item ? ui.item.value : $(element).val();
                if (ko.isWriteableObservable(modelValue)) {
                    modelValue(valueToWrite );

                } else {  //write to non-observable
                    if (allBindings['_ko_property_writers'] && allBindings['_ko_property_writers']['jqAutoValue'])
                        allBindings['_ko_property_writers']['jqAutoValue'](valueToWrite );
                }
            };

            options.change = handleValueChange;
            options.select = handleValueChange;
        }

        //handle the choices being updated in a DO, so the update function doesn't have to do it each time the value is updated
        var mappedSource = ko.dependentObservable(function() {
            var source = unwrap(allBindings.jqAutoSource);
            var valueProp = unwrap(allBindings.jqAutoSourceValue);
            var labelProp = unwrap(allBindings.jqAutoSourceLabel) || valueProp;

            var mapped = ko.utils.arrayMap(source, function(item) {
                var result = {};
                result.label = labelProp ? unwrap(item[labelProp]) : unwrap(item).toString();  //show in pop-up choices
                result.value = valueProp ? unwrap(item[valueProp]) : unwrap(item).toString();  //value
                return result;
            });
            return mapped;
        });

        mappedSource.subscribe(function(newValue) {
            $(element).autocomplete("option", "source", newValue);
        });

        options.source = mappedSource();

        $(element).autocomplete(options);
    },
    update: function(element, valueAccessor, allBindingsAccessor, viewModel) {
        //update value based on a model change
        console.log("jqAuto::update");
        var allBindings = allBindingsAccessor();
        var modelValue = allBindings.jqAutoValue;
        if (modelValue) {
            $(element).val(ko.utils.unwrapObservable(modelValue));
        }
    }
};
$( function () {
    ko.applyBindings(viewModel, document.getElementById("itemsTable"));
});
