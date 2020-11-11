(function () {
    'use strict';
    var designer = GC.Spread.Sheets.Designer;

    var filePlus = (function () {
        function filePlus() {

        }

        filePlus.prototype.openFromServer = function (spread, URL) {
        $("#loadingMask").show();
        $.ajax({
            url: URL,
            get: "GET",
            aysn:false,
            success: function (data) {
                var jsonS ={};
                try {
                    jsonS=   JSON.parse(data);
                } catch(err) {
                    $("#loadingMask").hide();
                    designer.MessageBox.show('格式化JSON错误', designer.res.title, 3 /* error */);
                }
                if(!jsonS.error){
                    try {
                        var host = window.location.host;
                        var protocol = document.location.protocol;
                        var ActionURL = protocol + "//" + host +"/"+ContextUrl+ "/webexcel/GetFileOnlyXls?fileName="+jsonS.fileName ;
                        $("#loadingMask").show();
                        designer.wrapper.reset();
                        var xhr = new XMLHttpRequest();
                        xhr.open('GET', ActionURL, true);
                        xhr.responseType = 'blob';
                        xhr.onload = function(e) {
                            if (this.status == 200) {
                                var blob = this.response;
                                var excelIo = new GC.Spread.Excel.IO();
                                    excelIo.open(blob, function (json) {
                                    $("#loadingMask").show();
                                    var workbookObj = json;
                                    designer.wrapper.spread.fromJSON(workbookObj);
                                    if(jsonS.plus){
                                        var JsSource=jsonS.plus.JsSource;
                                        //判断是否有JS插件，如果有，按照源码加载=====================================================
                                        jQuery.getScript(protocol + "//" + host +"/"+ContextUrl+ "/spreadjs/SpreadJS.Designer.13.1.0/fileMenu/"+"ComponentPlus.js")
                                            .done(function() {
                                                initComponentPlus(designer.wrapper.spread,jsonS.plus) ;
                                            })
                                            .fail(function() {
                                                return designer.MessageBox.show("不能加载JS插件"+JsSource, designer.res.title, 3 /* error */);
                                            })
                                    }
                                    $("#loadingMask").hide();
                                }, function (e) {
                                    $("#loadingMask").hide();
                                    designer.MessageBox.show(e.errorMessage, designer.res.title, 3 /* error */);
                                }, {});
                            }
                        };
                        xhr.send();

                        actions.currentFilePath = "";
                        actions.isFileModified = false;
                        updateWindowTitle();
                        designer.ribbon.updateRibbonBarStyle();
                        designer.wrapper.spreadElement.trigger("FileOpened");
                        designer.fileMenu.closeFileScreen();

                    } catch (ex) {
                        $("#loadingMask").hide();
                        designer.fileMenu.closeFileScreen();
                    }

                }else{
                    $("#loadingMask").hide();
                    return designer.MessageBox.show(jsonS.error, designer.res.title, 3 /* error */);
                }
                $("#loadingMask").hide();
            },
            error: function (ex) {
                $("#loadingMask").hide();
                return designer.MessageBox.show(designer.res.requestTempalteFail, designer.res.title, 3 /* error */);
            }
        });
        };

        filePlus.prototype.SaveToServer = function (spread, URL,cmd,target) {
           var serializationOption = {
            ignoreStyle: false, // indicate to ignore the style when convert workbook to json, default value is false
            ignoreFormula: false, // indicate to ignore the formula when convert workbook to json, default value is false
            rowHeadersAsFrozenColumns: false, // indicate to treat the row headers as frozen columns when convert workbook to json, default value is false
            columnHeadersAsFrozenRows: false // indicate to treat the column headers as frozen rows when convert workbook to json, default value is false
           }
        $("#loadingMask").show();

        var jsonStringPOST = JSON.stringify(spread.toJSON(serializationOption));
        var fd = new FormData(document.forms.namedItem("myform"));
        fd.append("JsonStr",jsonStringPOST);
        fd.append("target",target);
        fd.append("cmd",cmd);

        var formJsonStr = "";

        //遍历当前URL中所有的参数，然后for循环设置
        var parameterOfUrl = window.location.search;
        parameterOfUrl = parameterOfUrl.substr(1,parameterOfUrl.length);
        var parameterList = parameterOfUrl.split("&");
        for(var i=0;i<parameterList.length;i++){
            var parametersValue = parameterList[i].split("=");
            if (parametersValue[0] != "target"&&parametersValue[0]!="cmd"){
                fd.append(parametersValue[0],parametersValue[1]);
                formJsonStr=formJsonStr+"," + parametersValue[0]+":"+parametersValue[1];
            }
        }
        formJsonStr = "{"+formJsonStr.substr(1,formJsonStr.length)+"}";
        fd.append("formJsonStr",formJsonStr);

        $.ajax({
            url: URL,
            type:"POST",
            contentType:false,
            processData:false,
            data:fd,
            success: function (data) {
                $("#loadingMask").hide();
                return designer.MessageBox.show('服务器返回消息:'+JSON.parse(data).content, designer.res.title, 1);
            },
            error: function (ex) {
                $("#loadingMask").hide();
                return designer.MessageBox.show('保存出现错误~'+ex, designer.res.title, 3 );
            }
        });
        };
        return filePlus;
    })();

    designer.filePlus = filePlus;

    designer.loader.ready(function () {
		  if(getQueryVariable("target")){


            designer.filePlus = new filePlus();
            $("#loadingMask").show();
            var query = window.location.search.substring(1);
            var host = window.location.host;
            var protocol = document.location.protocol;
            var ActionURL = protocol + "//" + host +"/"+ContextUrl+ "/elfinder-servlet/connector?" + query;
            //先ajax查询后，判断无参数则直接显示，如果有必填参数，则显示界面，填写完毕参数后查询
            var target = getQueryVariable("target");
            $.ajax({
                url:  protocol + "//" + host+"/" +ContextUrl+ "/elfinder-servlet/connector?target=" + target+"&cmd=WebExcelParameters",
                get: "GET",
                aysn:false,
                success: function (data) {
                    try {
                        var jsrJson=   JSON.parse(data);
                        var json= jsrJson.Parameters;
                        if(json=='{}'){
                            json= JSON.parse(json);
                        }
                        if(jsrJson.error){
                             $("#loadingMask").hide();
                             designer.MessageBox.show(jsrJson.error, designer.res.title, 1 /* error */);
                        }else{
                            if(json.length>0){
                                var isFromUrl= getQueryVariable("isFromUrl");
                                if(isFromUrl){
                                    designer.filePlus.openFromServer(designer.wrapper.spread, ActionURL);
                                }else {
                                    var ExcelQueryParameter = new designer.ExcelQueryParameter();
                                    ExcelQueryParameter.open();
                                }
                            }else{
                               designer.filePlus.openFromServer(designer.wrapper.spread, ActionURL);
                            }
                        }
                       // $("#loadingMask").hide();
                    } catch(err) {
                        $("#loadingMask").hide();
                        console.error(err);
                       // $("#loadingMask").hide();
                    }
                },
                error: function (ex) {
                    console.error(err);
                    $("#loadingMask").hide();
                    return designer.MessageBox.show(designer.res.requestTempalteFail, designer.res.title, 3 /* error */);
                }
            });
			//此处可以动态追加功能
			$("#save-json-page").html("" +

                " <div class='submenu-title' >Save File To Cloud</div>" +
                " <div class='iconbutton OFL' id='save-json' data-action='SavetoServer'>" +
                " <div class='iconbutton-big-image xlsx-icon' /><span >Save  File</span>" +
                " </div>"+
                " <div class='iconbutton OFL' id='save-SSJSON' data-action='SavetoServer'>" +
                " <div class='iconbutton-big-image ssjson-icon' /><span >Save SSJSON File</span>" +
                " </div>"
            );

              if(getQueryVariable("target")){
              $("#save-json").on('click', function (e) {
				// 绑定菜单事件，代码重复待优化
                var host = window.location.host;
                var protocol = document.location.protocol;
                var SaveURL =  protocol + "//" + host +"/"+ContextUrl+"/elfinder-servlet/connector";
                designer.filePlus.SaveToServer(designer.wrapper.spread, SaveURL,"WebExcelSave",getQueryVariable("target"));
                event.preventDefault();
               });

              $("#save-SSJSON").on('click', function (e) {
                  var host = window.location.host;
                  var protocol = document.location.protocol;
                  var SaveURL =  protocol + "//" + host +"/"+ContextUrl+"/elfinder-servlet/connector";
                  designer.filePlus.SaveToServer(designer.wrapper.spread, SaveURL,"WebExcelSaveJson",getQueryVariable("target"));
                  event.preventDefault();
              });

             }else{
                  event.preventDefault();
                  designer.MessageBox.show('不能保存到服务器中..', '保存提示', 0 /* error */);
              }

			//绑定热键
			document.onkeydown = function() {
				// 判断 Ctrl+S
				if(event.ctrlKey == true && event.keyCode == 83) {
					if(getQueryVariable("target")){
						var host = window.location.host;
						var protocol = document.location.protocol;
						var SaveURL =  protocol + "//" + host +"/"+ContextUrl+ "/elfinder-servlet/connector";
						designer.filePlus.SaveToServer(designer.wrapper.spread, SaveURL,"WebExcelSave",getQueryVariable("target"));
						event.preventDefault();
					}else{
						event.preventDefault();
						designer.MessageBox.show('不能保存到服务器中..', '保存提示', 0 /* error */);
					}
				}
                if(event.ctrlKey == true && event.keyCode == 33) {//pageUp
                }

                if(event.ctrlKey == true && event.keyCode == 34) {//pageDown
                }
              //  event.returnValue=false;
            }


          }


    });
})();

