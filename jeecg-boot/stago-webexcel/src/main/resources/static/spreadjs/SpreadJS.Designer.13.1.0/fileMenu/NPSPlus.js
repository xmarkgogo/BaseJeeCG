(function () {
    'use strict';
    var designer = GC.Spread.Sheets.Designer;
    var NPSPlus = (function () {
        function NPSPlus() {}

        NPSPlus.prototype.openExcelFromServer = function (spread, URL){
            $("#loadingMask").show();
            var id= getQueryVariable("formId");
            var fid=getQueryVariable("fid");
            var conType=getQueryVariable("conType");
            var userType=getQueryVariable("userType");

            var excelIo = new GC.Spread.Excel.IO();
            var excelFilePath = URL+"&id="+id+"&fid="+fid+"&conType="+conType+"&userType="+userType;
            var xhr = new XMLHttpRequest();
            xhr.open('GET', excelFilePath, true);
            xhr.responseType = 'blob';
            xhr.onload = function(e) {
                if (this.status == 200) {
                    // get binary data as a response
                     var blob = this.response;
                    // convert Excel to JSON
                     excelIo.open(blob, function (json) {
                     var workbookObj = json;
                     spread.fromJSON(workbookObj);
                     /*   var sheetCount = spread.getSheetCount();
                         for (var i = 0, l = sheetCount; i < l; i++) {
                             var sheet=   spread.getSheet(i);
                             if(sheet.name()=='parameter' |  sheet.name()=='Calcul des marges Chine'){
                             }else{
                                 sheet.options.isProtected = true;
                                 sheet.options.protectionOptions = {
                                     allowSelectLockedCells: false,
                                     allowInsertRows: false,
                                     allowInsertColumns: false,
                                     allowDeleteColumns: false
                                 };
                             }
                         }*/
                     spread.setActiveSheetIndex(1); // change the active sheet.
                     $("#loadingMask").hide();
                    }, function (e) {
                       $("#loadingMask").hide();
                         designer.MessageBox.show(e.errorMessage, designer.res.title, 3 /* error */);
                    }, {});
                }
            };
           xhr.send();
        }
        //打开源文件
        NPSPlus.prototype.openFromServer = function (spread,URL) {
        var formId= getQueryVariable("formId");
        var fid=getQueryVariable("fid");
        var conType=getQueryVariable("conType");
        var userType=getQueryVariable("userType");
        $.ajax({
            url:URL,
            type:"POST",
            data: {"id":formId,"fid":fid,"conType":conType,"userType":userType},
            aysn:false,
            success: function (data) {
                try {
                    spread.fromJSON(data);
                    $("#loadingMask").show();
                    $.ajax({
                        url:"/a/nps/Request/getNPSExcel",
                        type:"POST",
                        data: {"id":formId,"fid":fid,"conType":conType,"userType":userType},
                        aysn:false,
                        success: function (data) {
                            var JsonQueryResult=data.ExcelJson;
                            for(var key in JsonQueryResult){
                                var DataSheet =spread.getSheetFromName(key);
                                DataSheet.suspendPaint();
                                if(DataSheet==null){ alert("缺少-"+key)};
                                var SheetJson= JsonQueryResult[key];
                                for(var i=0,l=SheetJson.length;i<l;i++) {
                                    //Excel坐标 1 1 开始，js Excel是 0 0 开始 所以强制 row-1  column-1
                                    var  dateType=SheetJson[i]["dateType"];
                                    if(dateType=="NUM" |dateType=="NUM1" | dateType=="NUM2" |  dateType=="INT"  ){
                                        DataSheet.setFormula( SheetJson[i]["row"]-1,SheetJson[i]["column"]-1, "=+"+SheetJson[i]["value"]);
                                    }else{
                                        DataSheet.setValue( SheetJson[i]["row"]-1,SheetJson[i]["column"]-1, SheetJson[i]["value"]);
                                    }
                                }

                                DataSheet.resumePaint();

                            }
                            $("#loadingMask").hide();
                        }
                    });

                } catch(err) {
                    $("#loadingMask").hide();
                    designer.MessageBox.show('读取源文件错误', designer.res.title, 3 /* error */);
                    console.error(err);
                }

            },
            error: function (ex) {
                $("#loadingMask").hide();
                return designer.MessageBox.show(designer.res.requestTempalteFail, designer.res.title, 3 /* error */);
            }
        });
        }
        //合并文件
        NPSPlus.prototype.Merge = function (spread,cmd) {
            var DataSheet = spread.getSheetFromName("parameter");
            var formId= getQueryVariable("formId");
            var MergFlag = DataSheet.getValue(5, 5);//6 行 6列
            var CoutrctNum = DataSheet.getValue(7, 5);
            var BeginMonth = DataSheet.getValue(9, 5);
            var smallCode = DataSheet.getValue(27,4);

            if ("00606" == smallCode){
                designer.MessageBox.show('小包装暂时不能进行合并方案处理.', '提示', 0 /* error */);
                return false;
            }
            if (MergFlag == "Y") {//处理合并数据
                if (BeginMonth < 1 || BeginMonth > 60) {
                    designer.fileMenu.closeFileScreen();
                    designer.MessageBox.show('开始合并月份不合理,请检查parameter页的F10.', '提示', 0 /* error */);
                    return false;
                }
                if (CoutrctNum == "" | CoutrctNum==null) {
                    designer.fileMenu.closeFileScreen();
                    designer.MessageBox.show('要合并的合同号为空,请检查parameter页的F8', '提示', 0 /* error */);
                    return false;
                }
                designer.fileMenu.closeFileScreen();
                var r = confirm("系统自动将调用旧方案数据覆盖部分新方案部分月份，如预览正确可保存");
                if (r != true) {
                    return false;
                }
                designer.NPSPlus.openExcelFromServer(designer.wrapper.spread,"/a/nps/Request/getNpsMergeExcel?CoutrctNum="+CoutrctNum+"&BeginMonth="+BeginMonth);
            }else{
                designer.fileMenu.closeFileScreen();
                designer.MessageBox.show('请确保合并方案标志位：为Y', '提示', 0 /* error */);
            }

        }

        NPSPlus.prototype.SaveToServer = function (spread,cmd) {

            designer.MessageBox.show('保存到服务器稍慢，请耐心等待弹出服务器提示信息~', '提示', 1 /* info */, 1 /* okCancel */, function (e, dialogResult) {
                switch (dialogResult) {
                    case 1 /* ok */:
                    {$("#loadingMask").show();
                        var json = spread.toJSON({includeBindingSource: true});
                        var fd = new FormData(document.forms.namedItem("myform"));
                        // 以下代码是实现只导出数据、删除公式、格式
                        var jsonNotFuc = json;
                        for (var i in jsonNotFuc.sheets) {
                            var dataTable = jsonNotFuc.sheets[i].data.dataTable;
                            for (var j in dataTable) {
                                for (var k in dataTable[j]) {
                                    var data = dataTable[j][k];
                                    var formula = data.formula;
                                    //删除公式
                                    if (typeof(formula) != "undefined") {
                                        // delete data.formula;
                                    }
                                }
                            }
                        }
                        fd.append("jsonNotFuc", JSON.stringify(jsonNotFuc));
                        var excelIo = new GC.Spread.Excel.IO();
                        excelIo.save(json, function (blob) {
                            console.error(blob);
                            fd.append("formId",   getQueryVariable("formId"));
                            fd.append("action",   cmd);
                            fd.append("taskid",   getQueryVariable("taskid"));
                            fd.append("fid",      getQueryVariable("fid"));
                            fd.append("excel", blob);

                            $.ajax({
                                url: "/a/nps/Request/superExcelUpdata",
                                type: "POST",
                                contentType: false,
                                processData: false,
                                data: fd,
                                beforeSend: function (XMLHttpRequest) {

                                },
                                success: function (data) {
                                    $("#loadingMask").hide();
                                    designer.MessageBox.show("服务器提示信息："+data.Info+".\r\n点击OK关闭当前窗口.", '提示', 1 /* info */, 1 /* okCancel */, function (e, dialogResult) {
                                        switch (dialogResult) {
                                            case 1 /* ok */:
                                                window.close();
                                            case 4 /* cancel */:
                                                window.close();
                                        }
                                    });

                                },
                                error: function (ex) {
                                    $("#loadingMask").hide();
                                    designer.MessageBox.show('保存失败..', '提示', 0 /* error */);
                                }
                            });
                        }, function (e) {
                            console.error(e);
                        });
                     }
                    case 4 /* cancel */:

                }
            });


     }
        return NPSPlus;
    })();
	
    designer.NPSPlus = NPSPlus;
    designer.loader.ready(function () {
        {
            designer.NPSPlus = new NPSPlus();

            var htmlAppend = "" +
                " <div class='submenu-title' >Action For Proposal </div>" +
                " <div class='iconbutton OFL' id='temp-json' data-action='SavetoServer'><div class='iconbutton-big-image xlsx-icon' /><span >保存</span> " +
                "</div>";
            if (getQueryVariable("FStatus") == '6'||getQueryVariable("FStatus") == '7') {
                htmlAppend = htmlAppend +  " <div class='iconbutton OFL' id='pass-json' data-action='SavetoServer'><div class='iconbutton-big-image xlsx-icon' /><span >通过</span> " +
                    "</div>" +
                    " <div class='iconbutton OFL' id='reject-json' data-action='SavetoServer'><div class='iconbutton-big-image xlsx-icon' /><span >拒绝</span> " +
                    "</div>" +
                    " <div class='iconbutton OFL' id='merge-json' data-action='Merge'><div class='iconbutton-big-image xlsx-icon' /><span >合并预览</span> " +
                    "</div>";
            }else if(getQueryVariable("FStatus")=='-1') {
                htmlAppend ="";
            }



            //此处可以动态追加功能
            $("#save-json-page").html("" +htmlAppend +"");

            $("#temp-json").on('click', function (e) {
                designer.NPSPlus.SaveToServer(designer.wrapper.spread, "Save");
                event.preventDefault();
            });

            $("#pass-json").on('click', function (e) {
                designer.NPSPlus.SaveToServer(designer.wrapper.spread, "SaveAndNextStep");
                event.preventDefault();
            });

            $("#reject-json").on('click', function (e) {
                designer.NPSPlus.SaveToServer(designer.wrapper.spread, "SaveAndNotConfirm");
                event.preventDefault();
            });
            $("#merge-json").on('click', function (e) {

                designer.NPSPlus.Merge(designer.wrapper.spread, "Merge");
                event.preventDefault();
            });

            // if(getQueryVariable("FStatus")=='-1') {//非流程编辑环节下，不能保存 }else{
            //   $("#pass-json,#reject-json,#merge-json,#temp-json").on('click', function (e) {
            //       event.preventDefault();
            //       designer.MessageBox.show('执行中的方案,仅限查看导出不能保存..', '保存提示', 0 /* error */);
            //   });
            // }

             // designer.NPSPlus.openFromServer(designer.wrapper.spread,"/a/nps/Request/getTempJson");
              designer.NPSPlus.openExcelFromServer(designer.wrapper.spread,"/a/nps/Request/getNPSExcel?1=1&userType=coo");
              // designer.NPSPlus.openExcelFromServer(designer.wrapper.spread,"/jeesite_war_exploded/a/nps/Request/getNPSExcel?1=1");
              designer.wrapper.spread.commandManager().addListener("anyscLicenser",function(){
                  for(var i=0;i<arguments.length;i++){
                      var cmd = arguments[i].command;
                      console.log(cmd);
                  }
              });

			//绑定热键
			document.onkeydown = function() {
				// 判断 Ctrl+S
				if(event.ctrlKey == true && event.keyCode == 83) {
                    designer.NPSPlus.SaveToServer(designer.wrapper.spread,"SaveAndNextStep");
                    event.preventDefault();
                 //   console.info("TEST---Hot Key");
                   // event.preventDefault();

                   // var sheet =  designer.wrapper.spread.getActiveSheet();
                 // designer.wrapper.spread.commandManager().execute({cmd: "navigationPreviousSheet", sheetName: sheet.name()});

				}
                if(event.ctrlKey == true && event.keyCode == 33) {//pageUp
                    event.returnvalue = false;
                    var sheet =  designer.wrapper.spread.getActiveSheet();
                    designer.wrapper.spread.commandManager().execute({cmd: "navigationPreviousSheet", sheetName: sheet.name()});
                  //  event.preventDefault();
                    console.info("TEST---Hot Key1");
                }
                if(event.ctrlKey == true && event.keyCode == 34) {//pageDown navigationNextSheet navigationNextSheet
                    event.returnvalue = false;
                  //  var sheet =  designer.wrapper.spread.getActiveSheet();
                  //  designer.wrapper.spread.commandManager().execute({cmd: "navigationNextSheet", sheetName: sheet.name()});
                   // event.preventDefault();
                   // console.info("TEST---Hot Key2");
                }
                //console.info( event.keyCode);
            }

          }

    });
})();

