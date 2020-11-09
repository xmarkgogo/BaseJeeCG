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
            var isHaveFileUtil=getQueryVariable("isHaveFileUtil");
            var excelIo = new GC.Spread.Excel.IO();
            var excelFilePath = URL+"&id="+id+"&fid="+fid+"&conType="+conType;
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
                     var sheetCount = spread.getSheetCount();

                     //针对sheet页进行保护
                     for (var i = 0, l = sheetCount; i < l; i++) {
                         var sheet=   spread.getSheet(i);

                         if(sheet.name()=='parameter' ){
                             //去除相应的折扣信息的保护
                             if(!isHaveFileUtil||"false" == isHaveFileUtil){
                                 var style = new GC.Spread.Sheets.Style();
                                 style.locked = false;
                                 style.backColor = 'lightGreen';
                                 for(var j =27;j<42;j++){
                                     sheet.setStyle(j,11,style);
                                     console.info(j);
                                 }
                                 sheet.setStyle(35,6,style);
                                 //放开填写备注的权限
                                 sheet.setStyle(13,7,style);
                                 $.ajax({
                                     type: "post",
                                     url: "/a/nps/CheckRule/getMaxDiscount",
                                     data: "FID="+id,
                                     success: function (data) {
                                         try {
                                             var sheetCount = spread.getSheetCount();
                                             //针对sheet页进行保护
                                             for (var i = 0, l = sheetCount; i < l; i++) {
                                                 var datasheet=   spread.getSheet(i);
                                                 if(datasheet.name()=='parameter' ){
                                                     for (var j = 0; j < data.length; j++) {
                                                         var productmaxdisc = data[j].productmaxdisc;
                                                         var productcode = data[j].productcode;
                                                         console.info(productcode + "--" + productmaxdisc)

                                                         /**
                                                          *
                                                          * createNumberValidator(6,0,5,true);
                                                          * 6:Between
                                                          * 0:最小值
                                                          * 5:最大值
                                                          * true:是整数
                                                          * <option value="6" selected>Between</option>
                                                            <option value="7">NotBetween</option>
                                                          <option value="0">EqualTo</option>
                                                          <option value="1">NotEqualTo</option>
                                                          <option value="2">GreaterThan</option>
                                                          <option value="4">LessThan</option>
                                                          <option value="3">GreaterThanOrEqualTo</option>
                                                          <option value="5">LessThanOrEqualTo</option>
                                                          */
                                                         var dv3 = new GC.Spread.Sheets.DataValidation.createNumberValidator(6,-100,productmaxdisc,true);//设置单元格数字验证
                                                         dv3.inputTitle("Please do not exceed your maximum discount permission");
                                                         dv3.inputMessage("您最大的折扣权限为"+productmaxdisc);
                                                         dv3.highlightStyle({
                                                             type: GC.Spread.Sheets.DataValidation.HighlightType.dogEar,
                                                             color: "red",
                                                             position: GC.Spread.Sheets.DataValidation.HighlightPosition.topRight
                                                         });
                                                         datasheet.setDataValidator(27 +j, 11, dv3);
                                                     }
                                                 }
                                             }
                                         } catch (e) {
                                             console.info(e)
                                         }
                                    }
                                 });
                             }
                         }else {

                             if(sheet.name()=='Etude Eco Réelle'||sheet.name()=='Group Summary'){
                                 sheet.visible(false);
                             }



                             sheet.options.isProtected = true;
                         }
                     }
                     spread.setActiveSheetIndex(1); // change the active sheet.
                     //    去除sheet页的邮件选择项
                     var newMenuData = [];
                     spread.contextMenu.menuData = newMenuData;
                     //去除导出功能
                     var as = document.getElementsByTagName("a");
                     for(var i = 0; i < as.length; i++) {
                         var txt = as[i].innerText || as[i].textContent;
                         if("Export"==txt){
                             as[i].parentNode.removeChild(as[i]);
                         }
                     }

                     $("#loadingMask").hide();
                    }, function (e) {
                         console.info(e);
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

        $.ajax({
            url:URL,
            type:"POST",
            data: {"id":formId,"fid":fid,"conType":conType},
            aysn:false,
            success: function (data) {
                try {
                    spread.fromJSON(data);
                    $("#loadingMask").show();
                    $.ajax({
                        url:"/a/nps/Request/getNsdStepNPSExcel",
                        type:"POST",
                        data: {"id":formId,"fid":fid,"conType":conType},
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

        NPSPlus.prototype.SaveToServer = function (spread,cmd) {
            var formId= getQueryVariable("formId");
            var isPass = true;
            $.ajax({
                type: "post",
                url: "/a/nps/CheckRule/getMaxDiscount",
                data: "FID="+formId,
                success: function (data) {

                    for (var j = 0; j < data.length; j++) {
                        var productmaxdisc = data[j].productmaxdisc;
                        var productcode = data[j].productcode;
                        var sheetCount = spread.getSheetCount();
                        //针对sheet页进行保护
                        for (var i = 0, l = sheetCount; i < l; i++) {
                            var datasheet=   spread.getSheet(i);
                            if(datasheet.name()=='parameter' ){
                                var code = datasheet.getValue(j+27,4);
                                var dicountByNsd = datasheet.getValue(j+27,11);
                                if (code == productcode){
                                    if(dicountByNsd>productmaxdisc){
                                        designer.MessageBox.show("产品:"+code+",您设定的折扣大于最大折扣"+productmaxdisc+",请修改折扣.", designer.res.title, 3 /* error */);
                                        isPass =false;
                                    }
                                }
                            }
                        }
                    }
                    if(isPass){

                        if ("SaveAndPublic" == cmd) {
                            $.ajax({
                                type:"post",
                                url :"/a/nps/CheckRule/CheckValidate",
                                data: {"FID":formId},
                                datatype:"json",
                                success:function(json){
                                    if(json.Status=="Pass"){
                                        saveToserver(spread,cmd);
                                    }else{
                                        console.error(json.Info);
                                        designer.MessageBox.show(json.Info, designer.res.title, 3 /* error */);

                                    }
                                }
                            });

                        }else {
                            saveToserver(spread,cmd);
                        }
                    }

                }
                });


     }
        return NPSPlus;
    })();

    function saveToserver(spread,cmd) {

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
                            url: "/a/nps/Request/superNsdExcelUpdate",
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
	
    designer.NPSPlus = NPSPlus;
    designer.loader.ready(function () {
        {
            designer.NPSPlus = new NPSPlus();

            var htmlAppend = "" +
                " <div class='submenu-title' >Action For Proposal </div>" ;
            if (getQueryVariable("FStatus") == '4') {
                htmlAppend = htmlAppend +  " <div class='iconbutton OFL' id='temp-json' data-action='SavetoServer'><div class='iconbutton-big-image xlsx-icon' /><span >暂存方案</span> " +
                    "</div>"+
                    " <div class='iconbutton OFL' id='pass-json' data-action='SavetoServer'><div class='iconbutton-big-image xlsx-icon' /><span >提交方案</span> " +
                    "</div>" +
                    " <div class='iconbutton OFL' id='reject-json' data-action='SavetoServer'><div class='iconbutton-big-image xlsx-icon' /><span >拒绝方案</span> " +
                    "</div>"
                    +" <div class='iconbutton OFL' id='publish-json' data-action='SavetoServer'><div class='iconbutton-big-image xlsx-icon' /><span >发布方案</span> " +
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
            $("#publish-json").on('click', function (e) {

                designer.NPSPlus.SaveToServer(designer.wrapper.spread, "SaveAndPublic");
                event.preventDefault();
            });

            // if(getQueryVariable("FStatus")=='-1') {//非流程编辑环节下，不能保存 }else{
            //   $("#pass-json,#reject-json,#merge-json,#temp-json").on('click', function (e) {
            //       event.preventDefault();
            //       designer.MessageBox.show('执行中的方案,仅限查看导出不能保存..', '保存提示', 0 /* error */);
            //   });
            // }

              designer.NPSPlus.openExcelFromServer(designer.wrapper.spread,"/a/nps/Request/getNsdStepNPSExcel?1=1");
              designer.wrapper.spread.commandManager().addListener("anyscLicenser",function(){
                  for(var i=0;i<arguments.length;i++){
                      var cmd = arguments[i].command;
                      console.log(cmd);
                      var sheetName = cmd.sheetName;
                      var newValue = cmd.newValue;
                      var row = cmd.row;
                      var col = cmd.col;

                      if  ("parameter"==sheetName&&row == 35 &&col == 6){
                          var sheetCount = designer.wrapper.spread.getSheetCount();

                          //针对sheet页进行保护
                          for (var i = 0, l = sheetCount; i < l; i++) {
                              var sheet=   designer.wrapper.spread.getSheet(i);

                              if(sheet.name()=='Etude Eco Réelle' ){
                                  sheet.setValue(63,15,newValue);
                              }
                          }
                      }

                  }
              });

			//绑定热键
			document.onkeydown = function() {
				// 判断 Ctrl+S

                    if(event.ctrlKey == true && event.keyCode == 83) {

                        if (getQueryVariable("FStatus") == '4') {
                            designer.NPSPlus.SaveToServer(designer.wrapper.spread,"SaveAndNextStep");
                            event.preventDefault();
                        //   console.info("TEST---Hot Key");
                        // event.preventDefault();

                        // var sheet =  designer.wrapper.spread.getActiveSheet();
                        // designer.wrapper.spread.commandManager().execute({cmd: "navigationPreviousSheet", sheetName: sheet.name()});

                        }else {
                            alert("当前不是您的环节,无法保存到数据库.");
                            return null;
                        }
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

