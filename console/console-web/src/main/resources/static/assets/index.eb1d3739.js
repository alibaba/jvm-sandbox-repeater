import{_ as k}from"./index.70bdb397.js";/* empty css              *//* empty css               *//* empty css               *//* empty css                *//* empty css               */import{d as F,c as I,B as u,C as d,bq as J,br as Q,u as v,F as t,aH as e,aG as a,aM as m,aV as A,aW as L,bF as R,bi as C,bj as B,aD as f,aE as x,b$ as X,bH as U,bD as z,r as Y,e as $,bk as ee,w as te,aK as V,aN as S,aL as ae,aF as oe,bE as se,bN as ne,bB as ue,bC as ce,bR as le}from"./arco.46b62ede.js";import{g as re}from"./vue.4bab78c1.js";import{u as de}from"./loading.f82ebe9b.js";import{a as ie}from"./replay.72c39936.js";/* empty css              *//* empty css              *//* empty css               */import{r as _e,b as pe,c as me}from"./case.0ede9485.js";import{R as fe,a as ve,I as ye}from"./invocation.52076e09.js";import"./chart.42ea7243.js";import"./base.3c7b4f95.js";const N=o=>(C("data-v-6dab2d84"),o=o(),B(),o),he={class:"diff-container"},be=N(()=>t("p",null,"response\u6BD4\u5BF9\u7ED3\u679C",-1)),ge={style:{"margin-bottom":"5px"}},xe={class:"arco-table-cell arco-table-cell-align-left"},Ie={class:"arco-table-td-content"},ke={class:"arco-empty"},Fe={class:"arco-empty-image"},Ce={viewBox:"0 0 48 48",fill:"none",xmlns:"http://www.w3.org/2000/svg",stroke:"currentColor",class:"arco-icon arco-icon-empty","stroke-width":"4","stroke-linecap":"butt","stroke-linejoin":"miter",style:{color:"green"}},Be=N(()=>t("path",{d:"M24 5v6m7 1 4-4m-18 4-4-4m28.5 22H28s-1 3-4 3-4-3-4-3H6.5M40 41H8a2 2 0 0 1-2-2v-8.46a2 2 0 0 1 .272-1.007l6.15-10.54A2 2 0 0 1 14.148 18H33.85a2 2 0 0 1 1.728.992l6.149 10.541A2 2 0 0 1 42 30.541V39a2 2 0 0 1-2 2Z"},null,-1)),we=[Be],De=N(()=>t("div",{class:"arco-empty-description",style:{color:"green"}},"\u6BD4\u5BF9\u901A\u8FC7",-1)),Ee={name:"DiffResp"},$e=F({...Ee,props:{replay:{type:Object,default(){return{differences:[]}}}},setup(o){const s=o,i=[{title:"\u8282\u70B9",dataIndex:"nodeName",width:100},{title:"\u671F\u671B",dataIndex:"expect",width:300},{title:"\u5B9E\u9645",dataIndex:"actual",width:300}],l=I(()=>s.replay.differences?s.replay.differences.length>0:!1),_=I(()=>s.replay.differences),r=()=>{_e(s.replay.caseId,s.replay.repeatId).then(c=>{A.success("\u66FF\u6362\u6210\u529F")})};return(c,y)=>{const n=L,h=R;return u(),d("div",he,[be,J(t("div",ge,[e(n,{type:"primary",shape:"round",size:"mini",onClick:r},{default:a(()=>[m("\u66FF\u6362")]),_:1})],512),[[Q,v(l)]]),e(h,{"row-key":"id",columns:i,data:v(_)},{empty:a(()=>[t("span",xe,[t("span",Ie,[t("div",ke,[t("div",Fe,[(u(),d("svg",Ce,we))]),De])])])]),_:1},8,["data"])])}}});const Se=k($e,[["__scopeId","data-v-6dab2d84"]]);const P=o=>(C("data-v-5240a157"),o=o(),B(),o),Ae=P(()=>t("span",null,"\u539F\u59CB\u5165\u53C2",-1)),Re=P(()=>t("span",null,"\u5B9E\u9645\u5165\u53C2",-1)),Ne={name:"ExpandSubInvoke"},He=F({...Ne,props:{caseId:String,repeatId:String,subInvocation:{type:Object,default(){return{diffs:[],originArgs:"",currentArgs:"",originUri:"",currentUri:"",index:1}}}},setup(o){const s=o,i=[{title:"\u8282\u70B9",dataIndex:"nodeName"},{title:"\u671F\u671B",dataIndex:"expect"},{title:"\u5B9E\u9645",dataIndex:"actual"}],l=I(()=>s.subInvocation.diffs?s.subInvocation.diffs:[]),_=()=>{s.caseId&&s.repeatId&&pe(s.caseId,s.repeatId,s.subInvocation.originUri,s.subInvocation.index).then(r=>{A.success("\u66FF\u6362\u6210\u529F")})};return(r,c)=>{const y=L,n=R,h=X,b=U,p=z;return u(),f(p,{class:"general-card",title:"\u5B50\u8C03\u7528\u8BE6\u60C5",style:{"background-color":"cornsilk"}},{default:a(()=>[v(l).length>0?(u(),f(y,{key:0,type:"primary",size:"mini",onClick:_},{default:a(()=>[m("\u66FF\u6362")]),_:1})):x("",!0),v(l).length>0?(u(),f(n,{key:1,"row-key":"id",columns:i,data:v(l)},null,8,["data"])):x("",!0),e(b,null,{default:a(()=>[Ae,e(h,{placeholder:"Please enter something","default-value":s.subInvocation.originArgs},null,8,["default-value"])]),_:1}),e(b,null,{default:a(()=>[Re,e(h,{placeholder:"Please enter something","default-value":s.subInvocation.currentArgs},null,8,["default-value"])]),_:1})]),_:1})}}});const Me=k(He,[["__scopeId","data-v-5240a157"]]),Te=o=>(C("data-v-26406c72"),o=o(),B(),o),Oe={class:"diff-container"},Ve={key:0},je=Te(()=>t("span",{class:"arco-table-cell arco-table-cell-align-left"},[t("span",{class:"arco-table-td-content"},[t("div",{class:"arco-empty"},[t("div",{class:"arco-empty-image"},[t("svg",{viewBox:"0 0 48 48",fill:"none",xmlns:"http://www.w3.org/2000/svg",stroke:"currentColor",class:"arco-icon arco-icon-empty","stroke-width":"4","stroke-linecap":"butt","stroke-linejoin":"miter"},[t("path",{d:"M24 5v6m7 1 4-4m-18 4-4-4m28.5 22H28s-1 3-4 3-4-3-4-3H6.5M40 41H8a2 2 0 0 1-2-2v-8.46a2 2 0 0 1 .272-1.007l6.15-10.54A2 2 0 0 1 14.148 18H33.85a2 2 0 0 1 1.728.992l6.149 10.541A2 2 0 0 1 42 30.541V39a2 2 0 0 1-2 2Z"})])]),t("div",{class:"arco-empty-description"},"\u6BD4\u5BF9\u901A\u8FC7")])])],-1)),Le={key:0,class:"success"},Ue={key:1,class:"fail"},ze={key:0,class:"success"},Pe={key:1,class:"fail"},qe={name:"MockInvocations"},Ge=F({...qe,props:{replay:{type:Object,default(){return{caseId:"",repeatId:"",mockInvocations:[]}}},showFailOnly:{type:Boolean,default:!1}},setup(o){const s=o,i=[{title:"index",dataIndex:"index"},{title:"\u5B50\u8C03\u7528Id",dataIndex:"currentUri"},{title:"\u5165\u53C2\u6BD4\u5BF9",dataIndex:"expect",slotName:"expect"},{title:"mock\u7ED3\u679C",dataIndex:"actual",slotName:"actual"}],l=I(()=>s.showFailOnly?s.replay.mockInvocations.filter(r=>!r.compareSuccess):s.replay.mockInvocations),_=Y({title:"\u8BE6\u60C5",width:80,expandedRowRender:()=>{}});return(r,c)=>{const y=R;return u(),d("div",Oe,[o.showFailOnly?(u(),d("p",Ve,"\u5B50\u8C03\u7528\u6BD4\u5BF9\u5931\u8D25\u96C6\u5408")):x("",!0),e(y,{"row-key":"index",columns:i,data:v(l),pagination:{pageSize:100},expandable:_,size:"small"},{empty:a(()=>[je]),expect:a(({record:n})=>[n.compareSuccess?(u(),d("span",Le,"\u6210\u529F")):(u(),d("span",Ue,"\u5931\u8D25"))]),actual:a(({record:n})=>[n.success?(u(),d("span",ze,"\u6210\u529F")):(u(),d("span",Pe,"\u5931\u8D25"))]),"expand-row":a(({record:n})=>[m(" >>>>>> \u5C55\u5F00 "),e(Me,{"sub-invocation":n,"case-id":o.replay.caseId,"repeat-id":o.replay.repeatId},null,8,["sub-invocation","case-id","repeat-id"])]),_:1},8,["data","expandable"])])}}});const j=k(Ge,[["__scopeId","data-v-26406c72"]]),w=o=>(C("data-v-0a17cdf1"),o=o(),B(),o),Ze={class:"table table-hover table-striped",width:"70%"},Ke=w(()=>t("td",{width:"80px",class:"text-right text-bold"},"\u6267\u884C\u7ED3\u679C",-1)),We={key:2},Je=w(()=>t("td",{width:"80px",class:"text-right text-bold"},"\u8FD0\u884C\u673A\u5668",-1)),Qe=w(()=>t("td",{width:"80px",class:"text-right text-bold"},"\u6267\u884C\u73AF\u5883",-1)),Xe=w(()=>t("td",{width:"80px",class:"text-right text-bold"},"\u6267\u884C\u65F6\u95F4",-1)),Ye={name:"Replay"},et=F({...Ye,setup(o){const{loading:s,setLoading:i}=de(!0),l=re(),{appName:_,repeatId:r}=l.params,c=$({environment:"",gmtCreate:"",status:"FINISH",success:!0,ip:"127.0.0.1",differences:[],caseId:"",appName:""}),y=()=>{i(!0),typeof _=="string"&&typeof r=="string"?ie(_,r).then(p=>{c.value=p.data,i(!1),c.value.status==="PROCESSING"?n==null&&(n=setInterval(()=>{y()},1e3)):n!==null&&(clearInterval(n),n=null)}):A.error("\u53C2\u6570\u4E0D\u6B63\u786E")};y();let n=null;ee(()=>{n!==null&&(clearInterval(n),n=null)});const h=$(!0),b=$();return te(()=>c.value.caseId,(p,H)=>{p!==H&&p&&me(p).then(D=>{b.value=D.data,h.value=!1})}),(p,H)=>{const D=V("Breadcrumb"),M=ae,q=oe,E=se,G=V("icon-loading"),Z=ne,g=ue,T=ce,K=U,O=z,W=le;return u(),f(W,{class:"container",loading:v(s),style:{width:"100%",height:"100%",padding:"0 20px 20px"}},{default:a(()=>[e(D),e(q,null,{default:a(()=>[e(M,null,{default:a(()=>[m("\u6D41\u91CF\u67E5\u8BE2")]),_:1}),e(M,null,{default:a(()=>[m("\u6D41\u91CF\u56DE\u653E")]),_:1})]),_:1}),v(s)?x("",!0):(u(),f(O,{key:0,class:"general-card",title:"\u56DE\u653E\u7ED3\u679C"},{default:a(()=>[e(K,null,{default:a(()=>[t("table",Ze,[t("tbody",null,[t("tr",null,[Ke,t("td",null,[c.value.status=="FINISH"?(u(),f(E,{key:0,color:"green"},{default:a(()=>[m("\u6210\u529F")]),_:1})):c.value.status=="FAILED"?(u(),f(E,{key:1,color:"red"},{default:a(()=>[m("\u5931\u8D25")]),_:1})):(u(),d("span",We,[e(G,{style:{color:"green"}}),e(E,{color:"green"},{default:a(()=>[m("\u8FD0\u884C\u4E2D")]),_:1})]))]),Je,t("td",null,S(c.value.ip),1),Qe,t("td",null,S(c.value.environment),1),Xe,t("td",null,S(c.value.gmtCreate),1)])])]),e(Z),e(T,{"default-active-key":"1",type:"rounded",style:{width:"100%"}},{default:a(()=>[e(g,{key:"1",title:"\u8FD4\u56DE\u7ED3\u679C\u5DEE\u5F02"},{default:a(()=>[e(Se,{replay:c.value},null,8,["replay"]),e(j,{replay:c.value,"show-fail-only":!0},null,8,["replay"])]),_:1}),e(g,{key:"2",title:"\u5B50\u8C03\u7528\u56DE\u653E\u8FC7\u7A0B"},{default:a(()=>[e(j,{replay:c.value},null,8,["replay"])]),_:1})]),_:1})]),_:1})]),_:1})),h.value?x("",!0):(u(),f(O,{key:1,class:"general-card",title:"\u91C7\u96C6\u4FE1\u606F",style:{"margin-top":"10px"}},{default:a(()=>[e(T,{"default-active-key":"1",type:"rounded"},{default:a(()=>[e(g,{key:"1",title:"\u8BF7\u6C42\u4FE1\u606F"},{default:a(()=>[e(fe,{record:b.value},null,8,["record"])]),_:1}),e(g,{key:"2",title:"\u8FD4\u56DE\u7ED3\u679C"},{default:a(()=>[e(ve,{record:b.value},null,8,["record"])]),_:1}),e(g,{key:"3",title:"\u5B50\u8C03\u7528"},{default:a(()=>[e(ye,{record:b.value},null,8,["record"])]),_:1})]),_:1})]),_:1}))]),_:1},8,["loading"])}}});const ht=k(et,[["__scopeId","data-v-0a17cdf1"]]);export{ht as default};