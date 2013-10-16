/**
 * Adobe Edge: symbol definitions
 */
(function($, Edge, compId){
//images folder
var im='images/';

var fonts = {};


var resources = [
];
var symbols = {
"stage": {
   version: "1.5.0",
   minimumCompatibleVersion: "1.5.0",
   build: "1.5.0.217",
   baseState: "Base State",
   initialState: "Base State",
   gpuAccelerate: false,
   resizeInstances: false,
   content: {
         dom: [
         {
            id:'Untitled-1',
            type:'image',
            rect:['0px','0px','720px','1280px','auto','auto'],
            fill:["rgba(0,0,0,0)",im+"Untitled-1.jpg",'0px','0px']
         },
         {
            id:'Text',
            type:'text',
            rect:['127px','552px','466px','176px','auto','auto'],
            text:"Baramej",
            align:"center",
            font:['Arial, Helvetica, sans-serif',123,"rgba(74,192,255,1.00)","600","none",""],
            textShadow:["rgba(0,0,0,0.65)",-7,3,3],
            transform:[]
         },
         {
            id:'Text2',
            type:'text',
            rect:['112px','998px','446px','96px','auto','auto'],
            text:"UnderConstruction",
            align:"center",
            font:['Arial, Helvetica, sans-serif',62,"rgba(255,0,0,1)","600","none","normal"]
         }],
         symbolInstances: [

         ]
      },
   states: {
      "Base State": {
         "${_Text}": [
            ["subproperty", "textShadow.blur", '0px'],
            ["subproperty", "textShadow.offsetH", '-14px'],
            ["subproperty", "filter.contrast", '0.97435897435897'],
            ["transform", "rotateZ", '0deg'],
            ["color", "color", 'rgba(255,0,0,1.00)'],
            ["style", "font-weight", '600'],
            ["style", "left", '112px'],
            ["style", "font-size", '141px'],
            ["style", "top", '670px'],
            ["subproperty", "filter.drop-shadow.blur", '0px'],
            ["transform", "skewY", '0deg'],
            ["transform", "skewX", '0deg'],
            ["subproperty", "filter.saturate", '0.74358974358974'],
            ["subproperty", "textShadow.color", 'rgba(0,0,0,0.65)'],
            ["subproperty", "textShadow.offsetV", '-13px'],
            ["style", "text-align", 'center']
         ],
         "${_Stage}": [
            ["color", "background-color", 'rgba(255,255,255,1)'],
            ["style", "min-width", '10%'],
            ["style", "overflow", 'hidden'],
            ["style", "height", '1280px'],
            ["style", "width", '720px']
         ],
         "${_Untitled-1}": [
            ["style", "left", '0px'],
            ["style", "top", '0px']
         ],
         "${_Text2}": [
            ["style", "top", '998px'],
            ["style", "left", '112px'],
            ["style", "font-size", '62px']
         ]
      }
   },
   timelines: {
      "Default Timeline": {
         fromState: "Base State",
         toState: "",
         duration: 2000,
         autoPlay: true,
         timeline: [
            { id: "eid35", tween: [ "transform", "${_Text}", "rotateZ", '360deg', { fromValue: '0deg'}], position: 500, duration: 1500 },
            { id: "eid31", tween: [ "style", "${_Text}", "top", '670px', { fromValue: '670px'}], position: 0, duration: 0 },
            { id: "eid30", tween: [ "style", "${_Text}", "left", '112px', { fromValue: '112px'}], position: 0, duration: 0 },
            { id: "eid5", tween: [ "subproperty", "${_Text}", "textShadow.offsetV", '8px', { fromValue: '-13px'}], position: 0, duration: 500 },
            { id: "eid6", tween: [ "subproperty", "${_Text}", "textShadow.offsetH", '6px', { fromValue: '-14px'}], position: 0, duration: 500 }         ]
      }
   }
}
};


Edge.registerCompositionDefn(compId, symbols, fonts, resources);

/**
 * Adobe Edge DOM Ready Event Handler
 */
$(window).ready(function() {
     Edge.launchComposition(compId);
});
})(jQuery, AdobeEdge, "EDGE-39380452");
