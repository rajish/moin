;(function($) {
	var app = $.sammy('#invoiceItemsTable', function() {
		this.debug = true;
		this.raise_errors = true;
		var form_fields = null;
		
		this.swap = function(content) {
			this.$element.hide('slow').html(content).show('slow');
		}
		
		this.get('#/', function(context) {
			context.log('main');
		});
		
		this.post('#/saveItem', function (context) {
			var item_def = new Sammy.Object();
			context.log('saveItem - params = ' + this.params);
			context.log('saveItem - form_fields = {');
			//for ( var item in this.params.keys()) {
			var items = this.params.keys(true);
			for ( var i = 0; i < items.length; i++) {
				var item = items[i];
				context.log('item: ' + item);
				if(item.match(/^item\./)) {
					item_def[':'+ item] = this.params[item];
				}
			}
			context.log('}');
			context.log('item_def: ' + item_def);
			var action = #{jsAction @saveItem(item_def) /};
			//context.log('action: ' + action);
			this.partial(action({item: item_def}));
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