(function () {
    'use strict';

    var keyword_undefined = void 0;
    var Spread = GC.Spread;
    var Sheets = Spread.Sheets;
    var designer = Sheets.Designer;

    var dialog2HtmlPath = designer.util.resolveHtmlPath('../dialogs/queryParameter', 'queryParameter.html');

    var ExcelQueryParameter = (function (_super) {
        designer.extends(ExcelQueryParameter, _super);
        function ExcelQueryParameter() {
            _super.call(this, (dialog2HtmlPath), '.Excel-Query-Parameter');
        }
        ExcelQueryParameter.prototype._initOptions = function () {
            var self = this;
            return {
                width: 'auto',
                modal: true,
                title: '设置查询参数-V1.0',
                buttons: [
                    {
                        text: designer.res.ok,
                        click: function () {
                            self._applySetting();
                            self.close();
                            designer.wrapper.setFocusToSpread();
                        }
                    },
                    {
                        text: designer.res.cancel,
                        click: function () {
                            self.close();
                            designer.wrapper.setFocusToSpread();
                        }
                    }
                ]
            };
        };

        //TODO//添加控件:下拉框/日期选择框
        ExcelQueryParameter.prototype._beforeOpen = function () {
            this.Parameters="";
            var that=this;
            if(getQueryVariable("target")){
                var target = getQueryVariable("target");
                var host = window.location.host;
                var protocol = document.location.protocol;
                var ActionURL = protocol + "//" + host +"/"+ContextUrl+ "/elfinder-servlet/connector?target=" + target+"&cmd=WebExcelParameters";
                $.ajax({
                    url: ActionURL,
                    get: "GET",
                    aysn:false,
                    success: function (data) {

                        try {
                            var  html_='';
                            var  jsScript_="";
                            var jsrJson= JSON.parse(data);
                            var json=jsrJson.Parameters;
                            if(json=='{}'){
                                json= JSON.parse(json);
                            }
                            that.Parameters=json;

                            //Java 接口应该返回 描述 是否必填  控件类型 select input... 日期选择等等。此处进行显示
                            for(var i=0,l=json.length;i<l;i++){
                                var htmlType = json[i].htmlType;
                                var isRequired = json[i].required;
                                // var dataJson= JSON.parse(data_List);
                                if ("" == htmlType||null== htmlType||"input" == htmlType){
                                    html_+=' <div class="sort-info"> <label class="sort-header" >'+json[i].desc+'</label>'
                                            + '<input id="'+json[i].name+'" value="'+json[i].value+'" '+(isRequired?'required="required"':'')
                                        +' class="sort-column"/>'
                                            +' <div class="clear-float"></div> </div>';
                                    jsScript_+="that."+json[i].name+"=that._element.find('#"+json[i].name+"');";//此处为关键代码

                                }else if("select" == htmlType){
                                    var data_List = json[i].data_List;
                                    var html_append = '<option value="">All</option>' ;
                                    for(var j=0,k=data_List.length;j<k;j++){
                                        html_append = html_append +'<option value="'+data_List[j].value+'">'+data_List[j].name+'</option>';
                                    }
                                    html_+=' <div class="sort-info"> <label class="sort-header" >'+json[i].desc+'</label>'
                                        + '<select id="'+json[i].name+'" name="'+json[i].name+'" class="sort-column">'
                                        + html_append
                                        +'</select> <div class="clear-float"></div> </div>';
                                    jsScript_+="that."+json[i].name+"=that._element.find('#"+json[i].name+"');";//此处为关键代码
                                }else if("radio" == htmlType){
                                    var data_List = json[i].data_List;
                                    var html_append='' ;
                                    for(var j=0,k=data_List.length;j<k;j++){
                                        html_append = html_append  +'<input id="'+json[i].name+'_'+j+'" name="'+json[i].name+'" value="'+data_List[j].value+'" '+(j==0?"checked":"")+' type="radio" />' +data_List[j].name;
                                        jsScript_+="that."+json[i].name+'_'+j+"=that._element.find('#"+json[i].name+'_'+j+"');";//此处为关键代码
                                    }
                                    // console.log(html_append)
                                    html_+=' <div class="sort-info"> <label class="sort-header" >'+json[i].desc+'</label>'
                                            + html_append
                                            + '<div class="clear-float"></div> </div>';

                                }else if("date" == htmlType){
                                   html_+=' <div class="sort-info"> <label class="sort-header" >'+json[i].desc+'</label>'
                                        // + '<input  name="'+json[i].name+'"  value=""  class="sort-column"  readonly="readonly" onclick="WdatePicker()"  />'
                                        + '日期下拉框'
                                       +'<div class="clear-float"></div> </div>';
                                }
                            }
                            that._element.find("#Excel-Query-Parameter_Dync").html(html_);
                            eval(jsScript_);
                            $("#loadingMask").hide();
                        } catch(err) {
                            $("#loadingMask").hide();
                            console.error(err);
                        }
                    },
                    error: function (ex) {
                        $("#loadingMask").hide();
                        return designer.MessageBox.show(designer.res.requestTempalteFail, designer.res.title, 3 /* error */);
                    }
                });
            }
        };
        ExcelQueryParameter.prototype._applySetting = function () {
            var Parstr="";
            var json=this.Parameters;
            for(var i=0,l=json.length;i<l;i++){
                var isRequired = json[i].required;
                var htmlType = json[i].htmlType;

                var name = "";
                var value_ = "";
                if("radio" == htmlType){
                    name=json[i].name;
                    var dataList = json[i].data_List;
                    for(var j=0 ,k=dataList.length;j<k;j++){
                        var isChecked = eval("this."+name+"_"+j+".prop('checked')") ;
                        if (isChecked){
                            value_ = eval("this."+name+"_"+j+".val()") ;
                            break;
                        }
                    }
                }else {
                    name=json[i].name;
                    value_=eval("this."+name+".val()");
                }
                //判断必填等必要的验证
                if (isRequired){
                    if (null==value_|"" == value_){
                        alert(name+":为必填,请填写.");
                        return designer.MessageBox.show(designer.res.requestTempalteFail, designer.res.title, 3 /* error */);
                    }
                }
                Parstr+="&"+name+"="+value_;
            }

            var query = window.location.search.substring(1);
            var host = window.location.host;
            var protocol = document.location.protocol;
            var ActionURL = protocol + "//" + host +"/"+ContextUrl+ "/elfinder-servlet/connector?" + query;
            designer.filePlus.openFromServer(designer.wrapper.spread, ActionURL+Parstr);
        };
        return ExcelQueryParameter;
    })(designer.BaseDialog);
    designer.ExcelQueryParameter = ExcelQueryParameter;
})();
function getQueryVariable(variable)
{
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == variable){return pair[1];}
    }
    return(false);
}
