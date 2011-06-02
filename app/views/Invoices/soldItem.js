;(function($) {
	var app = $.sammy('#invoiceItemsTable', function() {
		this.debug = true;
		var form_fields = null;
		
		this.swap = function(content) {
			this.$element.hide('slow').html(content).show('slow');
		}
		
		this.get('#/', function(context) {
			context.log('main');
		});
		
		this.post('#/saveItem', function (context) {
			form_fields = this.params;
			context.log('saveItem - form_fields = {');
			for ( var item in this.params) {
				context.log('item: ' + item);
			}
			context.log('}');
			var action = #{jsAction @saveItem(':item') /};
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
/*
var event = jQuery.Event("saveItem");
$("#saveItem").click(function(e) {
    //e.stopImmediatePropagation();
    this.trigger(event);
    log('clicked');
    //return false;
});
*/