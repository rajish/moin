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
/*
var event = jQuery.Event("saveItem");
$("#saveItem").click(function(e) {
    //e.stopImmediatePropagation();
    this.trigger(event);
    log('clicked');
    //return false;
});
*/