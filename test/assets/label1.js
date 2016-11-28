var test=require("js")

cc.Class({
    extends: test,

    properties: {
        // foo: {
        //    default: null,
        //    url: cc.Texture2D,  // optional, default is typeof default
        //    serializable: true, // optional, default is true
        //    visible: true,      // optional, default is true
        //    displayName: 'Foo', // optional
        //    readonly: false,    // optional, default is false
        // },
        // ...
    },

    // use this for initialization
    onLoad: function () {
        var self=this;
         this.node.on('mouseup',function(){
    
          this.node.dispatchEvent( new cc.Event.EventCustom('foobar', true) );
        });

    },

    // called every frame, uncomment this function to activate update callback
    // update: function (dt) {

    // },
});
