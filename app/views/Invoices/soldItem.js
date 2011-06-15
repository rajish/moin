;(function($) {
	var app = $.sammy('#invoiceItemsTable', function() {
		this.debug = true;
		this.raise_errors = true;
		var form_fields = null;
		
		this.swap = function(content) {
			this.$element().hide('slow').html(content).show('slow');
		}
		
		this.get('#/', function(context) {
			context.log('main');
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
								response(["&{'inventory.item.unknown'}"]);
							} else {
								response($.map(contents, function(item) {
									return {
										label: item.name + " - " + item.description,
										value: item.name
									};	
								}));
							}
						});
				},
				
				minLength: 0,
				
				select: function (event, ui) {
					Sammy.log("autocomplete::select");
				},
				
				open: function () {
					Sammy.log("autocomplete::open");
				},
				
				close: function () {
					Sammy.log("autocomplete::close");
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
		});
		
	});
	
	$(function(	) {
	
	
		app.run('#/');
	});
})(jQuery);
