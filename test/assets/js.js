cc.Class({
    extends: cc.Component,

    properties: {
       test:1,
       la:cc.Label
    },

    // use this for initialization
    onLoad: function () {
        
this.node.on('foobar', function (event) {
  alert(123);
});

    },

    // called every frame, uncomment this function to activate update callback
    // update: function (dt) {

    // },
});
