import{e as N}from"./index.70bdb397.js";/* empty css                *//* empty css               *//* empty css                *//* empty css              *//* empty css               */import{d as A,r as D,e as g,w as C,B as d,aD as E,aG as e,aM as s,aH as l,C as w,aJ as x,aI as S,aN as M,aV as O,b8 as q,b7 as L,bx as R,by as U,bL as I}from"./arco.46b62ede.js";import{g as P}from"./module.bf6e4bd0.js";const j={name:"ReplayConsole"},X=A({...j,props:{visible:{type:Boolean,default:!1,required:!0},appName:{type:String,default(){return""}},record:Object},emits:["update:visible","ok"],setup(m,{emit:i}){const r=m,c=N(),t=D({env:"test",ip:""}),f=r.appName===""?c.appName:r.appName,_=g(),h=()=>{if(t.ip===""){_.value.setFields({ip:{status:"error",message:"\u56DE\u653E\u673A\u5668\u5FC5\u9009"}});return}i("ok",t,r.record),i("update:visible",!1)},k=()=>{i("update:visible",!1)},v=g([]),p=()=>{f&&P(f,t.env).then(a=>{v.value=a.data,a.data.length>0?t.ip=a.data[0].ip:(O.warning("\u8BE5\u73AF\u5883\u65E0\u53EF\u7528agent\uFF0C\u8BF7\u6CE8\u610F\u56DE\u653E\u5B89\u5168\uFF01\uFF01"),t.ip="")})};C(()=>c.appName,(a,o)=>{a!==o&&p()},{deep:!0,immediate:!0}),C(()=>r.visible,(a,o)=>{a!==o&&a===!0&&p()},{deep:!0,immediate:!0});const y=a=>{p()};return(a,o)=>{const n=q,F=L,b=R,B=U,V=I;return d(),E(V,{visible:m.visible,onOk:h,onCancel:k},{title:e(()=>[s(" \u56DE\u653E\u63A7\u5236\u53F0 ")]),default:e(()=>[l(B,{ref_key:"formRef",ref:_,model:t,style:{width:"100%"}},{default:e(()=>[l(b,{field:"env",label:"\u73AF\u5883"},{default:e(()=>[l(F,{modelValue:t.env,"onUpdate:modelValue":o[0]||(o[0]=u=>t.env=u),placeholder:"Please select ...",onChange:y},{default:e(()=>[l(n,{value:"test",selected:""},{default:e(()=>[s("test")]),_:1}),l(n,{value:"dev",selected:""},{default:e(()=>[s("dev")]),_:1}),l(n,{value:"stg",selected:""},{default:e(()=>[s("stg")]),_:1})]),_:1},8,["modelValue"])]),_:1}),l(b,{field:"ip",label:"\u673A\u5668",rules:[{required:!0,message:"ip is required"}]},{default:e(()=>[l(F,{modelValue:t.ip,"onUpdate:modelValue":o[1]||(o[1]=u=>t.ip=u),placeholder:"Please select ..."},{default:e(()=>[(d(!0),w(x,null,S(v.value,u=>(d(),E(n,{key:u.ip,value:u.ip},{default:e(()=>[s(M(u.ip),1)]),_:2},1032,["value"]))),128))]),_:1},8,["modelValue"])]),_:1})]),_:1},8,["model"])]),_:1},8,["visible"])}}});export{X as _};