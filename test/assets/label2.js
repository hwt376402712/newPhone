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
        
this.node.on("123",function(event){
    alert(123)
})
    },

    // called every frame, uncomment this function to activate update callback
    // update: function (dt) {

    // },
});
