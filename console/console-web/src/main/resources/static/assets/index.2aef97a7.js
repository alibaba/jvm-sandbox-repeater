import{_ as y}from"./index.70bdb397.js";/* empty css               *//* empty css                *//* empty css               *//* empty css               */import{aK as l,B as b,C as f,aH as e,F as o,aG as r,aM as s,aN as a,aW as g,bb as h,bv as $,ba as k,bS as v,bT as B}from"./arco.46b62ede.js";import"./chart.42ea7243.js";import"./vue.4bab78c1.js";const T={name:"Error"};const L={class:"container"},w={class:"wrapper"},N={class:"details-wrapper"};function C(t,I,S,V,E,x){const i=l("Breadcrumb"),n=g,p=h,_=$,c=k,d=l("IconLink"),u=v,m=B;return b(),f("div",L,[e(i,{items:["menu.result","menu.result.error"]},null,8,["items"]),o("div",w,[e(_,{class:"result",status:"error",title:t.$t("error.result.title"),subtitle:t.$t("error.result.subTitle")},{extra:r(()=>[e(p,{class:"operation-wrap",size:16},{default:r(()=>[e(n,{key:"again",type:"secondary"},{default:r(()=>[s(a(t.$t("error.result.goBack")),1)]),_:1}),e(n,{key:"back",type:"primary"},{default:r(()=>[s(a(t.$t("error.result.retry")),1)]),_:1})]),_:1})]),_:1},8,["title","subtitle"]),o("div",N,[e(c,{heading:6,style:{"margin-top":"0"}},{default:r(()=>[s(a(t.$t("error.detailTitle")),1)]),_:1}),e(m,{style:{"margin-bottom":"0"}},{default:r(()=>[o("ol",null,[o("li",null,[s(a(t.$t("error.detailLine.record"))+" ",1),e(u,null,{default:r(()=>[e(d),s(" "+a(t.$t("error.detailLine.record.link")),1)]),_:1})]),o("li",null,a(t.$t("error.detailLine.auth")),1)])]),_:1})])])])}const R=y(T,[["render",C],["__scopeId","data-v-dd2a37cb"]]);export{R as default};