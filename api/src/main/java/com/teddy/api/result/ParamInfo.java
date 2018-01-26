package com.teddy.api.result;

/**
 * 接口的参数信息
 *
 * name : 取自RequestParam注解的信息 若没有 则取方法的参数名
 * type : 取自方法的参数类型
 * desc : 取自 方法注释中的 param 的注释
 * required、defaultValue均取自RequestParam注解的信息 没有就为默认值
 *
 * @author j.chen@91kge.com
 * create on 2017/8/18
 */
public class ParamInfo {
    private String name;
    private String type;
    private String desc;
    private Boolean required = Boolean.TRUE;
    private String defaultValue;

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
