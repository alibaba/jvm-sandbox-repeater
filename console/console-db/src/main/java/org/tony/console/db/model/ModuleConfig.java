package org.tony.console.db.model;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
*
*  @author author
*/
@Data
public class ModuleConfig implements Serializable {

    private static final long serialVersionUID = 1668489221521L;


    /**
    * 主键
    * 主键
    * isNullAble:0
    */
    private Long id;

    /**
    * 创建时间
    * isNullAble:0
    */
    private Date gmtCreate;

    /**
    * 录制时间
    * isNullAble:0
    */
    private Date gmtModified;

    /**
    * 应用名
    * isNullAble:0
    */
    private String appName;

    /**
    * 环境信息
    * isNullAble:0
    */
    private String environment;

    /**
    * 配置信息
    * isNullAble:0
    */
    private String config;



    @Override
    public String toString() {
        return "ModuleConfig{" +
                "id='" + id + '\'' +
                "gmtCreate='" + gmtCreate + '\'' +
                "gmtModified='" + gmtModified + '\'' +
                "appName='" + appName + '\'' +
                "environment='" + environment + '\'' +
                "config='" + config + '\'' +
            '}';
    }

    public static Builder Build(){return new Builder();}

    public static ConditionBuilder ConditionBuild(){return new ConditionBuilder();}

    public static UpdateBuilder UpdateBuild(){return new UpdateBuilder();}

    public static QueryBuilder QueryBuild(){return new QueryBuilder();}

    public static class UpdateBuilder {

        private ModuleConfig set;

        private ConditionBuilder where;

        public UpdateBuilder set(ModuleConfig set){
            this.set = set;
            return this;
        }

        public ModuleConfig getSet(){
            return this.set;
        }

        public UpdateBuilder where(ConditionBuilder where){
            this.where = where;
            return this;
        }

        public ConditionBuilder getWhere(){
            return this.where;
        }

        public UpdateBuilder build(){
            return this;
        }
    }

    public static class QueryBuilder extends ModuleConfig{
        /**
        * 需要返回的列
        */
        private Map<String,Object> fetchFields;

        public Map<String,Object> getFetchFields(){return this.fetchFields;}

        private List<Long> idList;

        public List<Long> getIdList(){return this.idList;}

        private Long idSt;

        private Long idEd;

        public Long getIdSt(){return this.idSt;}

        public Long getIdEd(){return this.idEd;}

        private List<Date> gmtCreateList;

        public List<Date> getGmtCreateList(){return this.gmtCreateList;}

        private Date gmtCreateSt;

        private Date gmtCreateEd;

        public Date getGmtCreateSt(){return this.gmtCreateSt;}

        public Date getGmtCreateEd(){return this.gmtCreateEd;}

        private List<Date> gmtModifiedList;

        public List<Date> getGmtModifiedList(){return this.gmtModifiedList;}

        private Date gmtModifiedSt;

        private Date gmtModifiedEd;

        public Date getGmtModifiedSt(){return this.gmtModifiedSt;}

        public Date getGmtModifiedEd(){return this.gmtModifiedEd;}

        private List<String> appNameList;

        public List<String> getAppNameList(){return this.appNameList;}


        private List<String> fuzzyAppName;

        public List<String> getFuzzyAppName(){return this.fuzzyAppName;}

        private List<String> rightFuzzyAppName;

        public List<String> getRightFuzzyAppName(){return this.rightFuzzyAppName;}
        private List<String> environmentList;

        public List<String> getEnvironmentList(){return this.environmentList;}


        private List<String> fuzzyEnvironment;

        public List<String> getFuzzyEnvironment(){return this.fuzzyEnvironment;}

        private List<String> rightFuzzyEnvironment;

        public List<String> getRightFuzzyEnvironment(){return this.rightFuzzyEnvironment;}
        private List<String> configList;

        public List<String> getConfigList(){return this.configList;}


        private List<String> fuzzyConfig;

        public List<String> getFuzzyConfig(){return this.fuzzyConfig;}

        private List<String> rightFuzzyConfig;

        public List<String> getRightFuzzyConfig(){return this.rightFuzzyConfig;}
        private QueryBuilder (){
            this.fetchFields = new HashMap<>();
        }

        public QueryBuilder idBetWeen(Long idSt,Long idEd){
            this.idSt = idSt;
            this.idEd = idEd;
            return this;
        }

        public QueryBuilder idGreaterEqThan(Long idSt){
            this.idSt = idSt;
            return this;
        }
        public QueryBuilder idLessEqThan(Long idEd){
            this.idEd = idEd;
            return this;
        }


        public QueryBuilder id(Long id){
            setId(id);
            return this;
        }

        public QueryBuilder idList(Long ... id){
            this.idList = solveNullList(id);
            return this;
        }

        public QueryBuilder idList(List<Long> id){
            this.idList = id;
            return this;
        }

        public QueryBuilder fetchId(){
            setFetchFields("fetchFields","id");
            return this;
        }

        public QueryBuilder excludeId(){
            setFetchFields("excludeFields","id");
            return this;
        }

        public QueryBuilder gmtCreateBetWeen(Date gmtCreateSt,Date gmtCreateEd){
            this.gmtCreateSt = gmtCreateSt;
            this.gmtCreateEd = gmtCreateEd;
            return this;
        }

        public QueryBuilder gmtCreateGreaterEqThan(Date gmtCreateSt){
            this.gmtCreateSt = gmtCreateSt;
            return this;
        }
        public QueryBuilder gmtCreateLessEqThan(Date gmtCreateEd){
            this.gmtCreateEd = gmtCreateEd;
            return this;
        }


        public QueryBuilder gmtCreate(Date gmtCreate){
            setGmtCreate(gmtCreate);
            return this;
        }

        public QueryBuilder gmtCreateList(Date ... gmtCreate){
            this.gmtCreateList = solveNullList(gmtCreate);
            return this;
        }

        public QueryBuilder gmtCreateList(List<Date> gmtCreate){
            this.gmtCreateList = gmtCreate;
            return this;
        }

        public QueryBuilder fetchGmtCreate(){
            setFetchFields("fetchFields","gmtCreate");
            return this;
        }

        public QueryBuilder excludeGmtCreate(){
            setFetchFields("excludeFields","gmtCreate");
            return this;
        }

        public QueryBuilder gmtModifiedBetWeen(Date gmtModifiedSt,Date gmtModifiedEd){
            this.gmtModifiedSt = gmtModifiedSt;
            this.gmtModifiedEd = gmtModifiedEd;
            return this;
        }

        public QueryBuilder gmtModifiedGreaterEqThan(Date gmtModifiedSt){
            this.gmtModifiedSt = gmtModifiedSt;
            return this;
        }
        public QueryBuilder gmtModifiedLessEqThan(Date gmtModifiedEd){
            this.gmtModifiedEd = gmtModifiedEd;
            return this;
        }


        public QueryBuilder gmtModified(Date gmtModified){
            setGmtModified(gmtModified);
            return this;
        }

        public QueryBuilder gmtModifiedList(Date ... gmtModified){
            this.gmtModifiedList = solveNullList(gmtModified);
            return this;
        }

        public QueryBuilder gmtModifiedList(List<Date> gmtModified){
            this.gmtModifiedList = gmtModified;
            return this;
        }

        public QueryBuilder fetchGmtModified(){
            setFetchFields("fetchFields","gmtModified");
            return this;
        }

        public QueryBuilder excludeGmtModified(){
            setFetchFields("excludeFields","gmtModified");
            return this;
        }

        public QueryBuilder fuzzyAppName (List<String> fuzzyAppName){
            this.fuzzyAppName = fuzzyAppName;
            return this;
        }

        public QueryBuilder fuzzyAppName (String ... fuzzyAppName){
            this.fuzzyAppName = solveNullList(fuzzyAppName);
            return this;
        }

        public QueryBuilder rightFuzzyAppName (List<String> rightFuzzyAppName){
            this.rightFuzzyAppName = rightFuzzyAppName;
            return this;
        }

        public QueryBuilder rightFuzzyAppName (String ... rightFuzzyAppName){
            this.rightFuzzyAppName = solveNullList(rightFuzzyAppName);
            return this;
        }

        public QueryBuilder appName(String appName){
            setAppName(appName);
            return this;
        }

        public QueryBuilder appNameList(String ... appName){
            this.appNameList = solveNullList(appName);
            return this;
        }

        public QueryBuilder appNameList(List<String> appName){
            this.appNameList = appName;
            return this;
        }

        public QueryBuilder fetchAppName(){
            setFetchFields("fetchFields","appName");
            return this;
        }

        public QueryBuilder excludeAppName(){
            setFetchFields("excludeFields","appName");
            return this;
        }

        public QueryBuilder fuzzyEnvironment (List<String> fuzzyEnvironment){
            this.fuzzyEnvironment = fuzzyEnvironment;
            return this;
        }

        public QueryBuilder fuzzyEnvironment (String ... fuzzyEnvironment){
            this.fuzzyEnvironment = solveNullList(fuzzyEnvironment);
            return this;
        }

        public QueryBuilder rightFuzzyEnvironment (List<String> rightFuzzyEnvironment){
            this.rightFuzzyEnvironment = rightFuzzyEnvironment;
            return this;
        }

        public QueryBuilder rightFuzzyEnvironment (String ... rightFuzzyEnvironment){
            this.rightFuzzyEnvironment = solveNullList(rightFuzzyEnvironment);
            return this;
        }

        public QueryBuilder environment(String environment){
            setEnvironment(environment);
            return this;
        }

        public QueryBuilder environmentList(String ... environment){
            this.environmentList = solveNullList(environment);
            return this;
        }

        public QueryBuilder environmentList(List<String> environment){
            this.environmentList = environment;
            return this;
        }

        public QueryBuilder fetchEnvironment(){
            setFetchFields("fetchFields","environment");
            return this;
        }

        public QueryBuilder excludeEnvironment(){
            setFetchFields("excludeFields","environment");
            return this;
        }

        public QueryBuilder fuzzyConfig (List<String> fuzzyConfig){
            this.fuzzyConfig = fuzzyConfig;
            return this;
        }

        public QueryBuilder fuzzyConfig (String ... fuzzyConfig){
            this.fuzzyConfig = solveNullList(fuzzyConfig);
            return this;
        }

        public QueryBuilder rightFuzzyConfig (List<String> rightFuzzyConfig){
            this.rightFuzzyConfig = rightFuzzyConfig;
            return this;
        }

        public QueryBuilder rightFuzzyConfig (String ... rightFuzzyConfig){
            this.rightFuzzyConfig = solveNullList(rightFuzzyConfig);
            return this;
        }

        public QueryBuilder config(String config){
            setConfig(config);
            return this;
        }

        public QueryBuilder configList(String ... config){
            this.configList = solveNullList(config);
            return this;
        }

        public QueryBuilder configList(List<String> config){
            this.configList = config;
            return this;
        }

        public QueryBuilder fetchConfig(){
            setFetchFields("fetchFields","config");
            return this;
        }

        public QueryBuilder excludeConfig(){
            setFetchFields("excludeFields","config");
            return this;
        }
        private <T>List<T> solveNullList(T ... objs){
            if (objs != null){
            List<T> list = new ArrayList<>();
                for (T item : objs){
                    if (item != null){
                        list.add(item);
                    }
                }
                return list;
            }
            return null;
        }

        public QueryBuilder fetchAll(){
            this.fetchFields.put("AllFields",true);
            return this;
        }

        public QueryBuilder addField(String ... fields){
            List<String> list = new ArrayList<>();
            if (fields != null){
                for (String field : fields){
                    list.add(field);
                }
            }
            this.fetchFields.put("otherFields",list);
            return this;
        }
        @SuppressWarnings("unchecked")
        private void setFetchFields(String key,String val){
            Map<String,Boolean> fields= (Map<String, Boolean>) this.fetchFields.get(key);
            if (fields == null){
                fields = new HashMap<>();
            }
            fields.put(val,true);
            this.fetchFields.put(key,fields);
        }

        public ModuleConfig build(){return this;}
    }


    public static class ConditionBuilder{
        private List<Long> idList;

        public List<Long> getIdList(){return this.idList;}

        private Long idSt;

        private Long idEd;

        public Long getIdSt(){return this.idSt;}

        public Long getIdEd(){return this.idEd;}

        private List<Date> gmtCreateList;

        public List<Date> getGmtCreateList(){return this.gmtCreateList;}

        private Date gmtCreateSt;

        private Date gmtCreateEd;

        public Date getGmtCreateSt(){return this.gmtCreateSt;}

        public Date getGmtCreateEd(){return this.gmtCreateEd;}

        private List<Date> gmtModifiedList;

        public List<Date> getGmtModifiedList(){return this.gmtModifiedList;}

        private Date gmtModifiedSt;

        private Date gmtModifiedEd;

        public Date getGmtModifiedSt(){return this.gmtModifiedSt;}

        public Date getGmtModifiedEd(){return this.gmtModifiedEd;}

        private List<String> appNameList;

        public List<String> getAppNameList(){return this.appNameList;}


        private List<String> fuzzyAppName;

        public List<String> getFuzzyAppName(){return this.fuzzyAppName;}

        private List<String> rightFuzzyAppName;

        public List<String> getRightFuzzyAppName(){return this.rightFuzzyAppName;}
        private List<String> environmentList;

        public List<String> getEnvironmentList(){return this.environmentList;}


        private List<String> fuzzyEnvironment;

        public List<String> getFuzzyEnvironment(){return this.fuzzyEnvironment;}

        private List<String> rightFuzzyEnvironment;

        public List<String> getRightFuzzyEnvironment(){return this.rightFuzzyEnvironment;}
        private List<String> configList;

        public List<String> getConfigList(){return this.configList;}


        private List<String> fuzzyConfig;

        public List<String> getFuzzyConfig(){return this.fuzzyConfig;}

        private List<String> rightFuzzyConfig;

        public List<String> getRightFuzzyConfig(){return this.rightFuzzyConfig;}

        public ConditionBuilder idBetWeen(Long idSt,Long idEd){
            this.idSt = idSt;
            this.idEd = idEd;
            return this;
        }

        public ConditionBuilder idGreaterEqThan(Long idSt){
            this.idSt = idSt;
            return this;
        }
        public ConditionBuilder idLessEqThan(Long idEd){
            this.idEd = idEd;
            return this;
        }


        public ConditionBuilder idList(Long ... id){
            this.idList = solveNullList(id);
            return this;
        }

        public ConditionBuilder idList(List<Long> id){
            this.idList = id;
            return this;
        }

        public ConditionBuilder gmtCreateBetWeen(Date gmtCreateSt,Date gmtCreateEd){
            this.gmtCreateSt = gmtCreateSt;
            this.gmtCreateEd = gmtCreateEd;
            return this;
        }

        public ConditionBuilder gmtCreateGreaterEqThan(Date gmtCreateSt){
            this.gmtCreateSt = gmtCreateSt;
            return this;
        }
        public ConditionBuilder gmtCreateLessEqThan(Date gmtCreateEd){
            this.gmtCreateEd = gmtCreateEd;
            return this;
        }


        public ConditionBuilder gmtCreateList(Date ... gmtCreate){
            this.gmtCreateList = solveNullList(gmtCreate);
            return this;
        }

        public ConditionBuilder gmtCreateList(List<Date> gmtCreate){
            this.gmtCreateList = gmtCreate;
            return this;
        }

        public ConditionBuilder gmtModifiedBetWeen(Date gmtModifiedSt,Date gmtModifiedEd){
            this.gmtModifiedSt = gmtModifiedSt;
            this.gmtModifiedEd = gmtModifiedEd;
            return this;
        }

        public ConditionBuilder gmtModifiedGreaterEqThan(Date gmtModifiedSt){
            this.gmtModifiedSt = gmtModifiedSt;
            return this;
        }
        public ConditionBuilder gmtModifiedLessEqThan(Date gmtModifiedEd){
            this.gmtModifiedEd = gmtModifiedEd;
            return this;
        }


        public ConditionBuilder gmtModifiedList(Date ... gmtModified){
            this.gmtModifiedList = solveNullList(gmtModified);
            return this;
        }

        public ConditionBuilder gmtModifiedList(List<Date> gmtModified){
            this.gmtModifiedList = gmtModified;
            return this;
        }

        public ConditionBuilder fuzzyAppName (List<String> fuzzyAppName){
            this.fuzzyAppName = fuzzyAppName;
            return this;
        }

        public ConditionBuilder fuzzyAppName (String ... fuzzyAppName){
            this.fuzzyAppName = solveNullList(fuzzyAppName);
            return this;
        }

        public ConditionBuilder rightFuzzyAppName (List<String> rightFuzzyAppName){
            this.rightFuzzyAppName = rightFuzzyAppName;
            return this;
        }

        public ConditionBuilder rightFuzzyAppName (String ... rightFuzzyAppName){
            this.rightFuzzyAppName = solveNullList(rightFuzzyAppName);
            return this;
        }

        public ConditionBuilder appNameList(String ... appName){
            this.appNameList = solveNullList(appName);
            return this;
        }

        public ConditionBuilder appNameList(List<String> appName){
            this.appNameList = appName;
            return this;
        }

        public ConditionBuilder fuzzyEnvironment (List<String> fuzzyEnvironment){
            this.fuzzyEnvironment = fuzzyEnvironment;
            return this;
        }

        public ConditionBuilder fuzzyEnvironment (String ... fuzzyEnvironment){
            this.fuzzyEnvironment = solveNullList(fuzzyEnvironment);
            return this;
        }

        public ConditionBuilder rightFuzzyEnvironment (List<String> rightFuzzyEnvironment){
            this.rightFuzzyEnvironment = rightFuzzyEnvironment;
            return this;
        }

        public ConditionBuilder rightFuzzyEnvironment (String ... rightFuzzyEnvironment){
            this.rightFuzzyEnvironment = solveNullList(rightFuzzyEnvironment);
            return this;
        }

        public ConditionBuilder environmentList(String ... environment){
            this.environmentList = solveNullList(environment);
            return this;
        }

        public ConditionBuilder environmentList(List<String> environment){
            this.environmentList = environment;
            return this;
        }

        public ConditionBuilder fuzzyConfig (List<String> fuzzyConfig){
            this.fuzzyConfig = fuzzyConfig;
            return this;
        }

        public ConditionBuilder fuzzyConfig (String ... fuzzyConfig){
            this.fuzzyConfig = solveNullList(fuzzyConfig);
            return this;
        }

        public ConditionBuilder rightFuzzyConfig (List<String> rightFuzzyConfig){
            this.rightFuzzyConfig = rightFuzzyConfig;
            return this;
        }

        public ConditionBuilder rightFuzzyConfig (String ... rightFuzzyConfig){
            this.rightFuzzyConfig = solveNullList(rightFuzzyConfig);
            return this;
        }

        public ConditionBuilder configList(String ... config){
            this.configList = solveNullList(config);
            return this;
        }

        public ConditionBuilder configList(List<String> config){
            this.configList = config;
            return this;
        }

        private <T>List<T> solveNullList(T ... objs){
            if (objs != null){
            List<T> list = new ArrayList<>();
                for (T item : objs){
                    if (item != null){
                        list.add(item);
                    }
                }
                return list;
            }
            return null;
        }

        public ConditionBuilder build(){return this;}
    }

    public static class Builder {

        private ModuleConfig obj;

        public Builder(){
            this.obj = new ModuleConfig();
        }

        public Builder id(Long id){
            this.obj.setId(id);
            return this;
        }
        public Builder gmtCreate(Date gmtCreate){
            this.obj.setGmtCreate(gmtCreate);
            return this;
        }
        public Builder gmtModified(Date gmtModified){
            this.obj.setGmtModified(gmtModified);
            return this;
        }
        public Builder appName(String appName){
            this.obj.setAppName(appName);
            return this;
        }
        public Builder environment(String environment){
            this.obj.setEnvironment(environment);
            return this;
        }
        public Builder config(String config){
            this.obj.setConfig(config);
            return this;
        }
        public ModuleConfig build(){return obj;}
    }

}
