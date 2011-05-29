;(function($) {
	var app = $.sammy('#invoiceItemsTable', function() {
		this.debug = true;
		var form_fields = null;
		
		this.get('#/', function(context) {
			context.log('main');
		});
		
		this.get('#/saveItem', function (context) {
			context.log('saveItem');
			form_fields = this.params;
			var action = #{jsAction @saveItem(':item')};
			this.partial(action({item: form_fields}));
		});
		
		this.get('#/cancelEdits', function (context) {
			context.log('cancelEdits');
		});
	});
	
	$(function(	) {
		app.run('#/');
	});
})(jQuery);
