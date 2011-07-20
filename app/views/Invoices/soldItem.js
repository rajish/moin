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

        this.post('#/saveItem', function (context) {
            var items = this.params.toHash();
            var action = #{jsAction @Invoices.saveItem() /};
            this.send($.post, action(), items)
                .then(function(contents) {
                    this.swap(contents);
                });
        });

        this.get('#/cancelEdits', function (context) {
            context.log('cancelEdits');
            ('#itemsTable').reset();
        });

    });


    $(function( ) {
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

    	var viewModel = {
                itemPrice: ko.observable(0),
                itemDiscount: ko.observable(0),
                itemQty: ko.observable(1),
                itemVat: ko.observable(0),
                itemNotes: ko.observable('')
        };

        viewModel.itemNettVal = ko.dependentObservable(function () {
            var retval = parseFloat(viewModel.itemPrice()) * parseFloat(viewModel.itemQty()) * (1 - parseFloat(viewModel.itemDiscount())/100);
            Sammy.log("KO:itemNettVal recalculation: " + retval);
            return retval.toMoney();
        });

        viewModel.itemTotVal = ko.dependentObservable(function (){
            var retval = parseFloat(viewModel.itemNettVal()) * (1 + parseFloat(viewModel.itemVat())/100);
            Sammy.log("KO:itemTotVal recalculation: " + retval + " vat factor: " + (1 + parseFloat(viewModel.itemVat())/100));
            return retval.toMoney();
        });

        ko.applyBindings(viewModel);
        app.run('#/');
    });
})(jQuery);
