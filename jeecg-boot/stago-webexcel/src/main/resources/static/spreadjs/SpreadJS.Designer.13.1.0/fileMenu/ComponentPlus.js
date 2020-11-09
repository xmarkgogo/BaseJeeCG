function initComponentPlus(spread,DataJson) {

    /*
     *  注意：这里没有直接删除原数组，而是创建一个新数组，
     *  原因是在遍历数组时不能同时删除数组项，这样会导致程序运算结果错误。

    var menuData = spread.contextMenu.menuData;
    var newMenuData = [];
    menuData.forEach(function (item) {
        if(item){
            if(item.name === "gc.spread.insertSheet" ||
                item.name === "gc.spread.deleteSheet"){
                return;
            }
           // newMenuData.push(item);
        }
    });

   // 将新数组赋予spread的右键菜单属性
    spread.contextMenu.menuData = newMenuData;

     * */
    var sheet = spread.getSheet(0);
    sheet.suspendPaint();


    for(var i=0;i<DataJson.length ;i++) {
        JsonObj=DataJson[i];
        cellRange=JsonObj.cellRange;
        cellType = JsonObj.cellType;
        modelid  =JsonObj.modelid;
        sheetName=JsonObj.sheetName;
        var ItemsDataJson={};
        if(typeof(modelid)!="undefined"){
            ItemsDataJson=JsonObj.modelData;
        }

        var CellArr= new Array();
        //判断范围 首先是逗号分隔 ,然后判断是否是冒号分隔，然后判断最小单位中是否带有数字，如果带有数字则是具体的单元格，不带有数字则是指定的列。
        var cellRangeSplitArr= new Array();      //定义一数组
        cellRangeSplitArr=cellRange.split(",");  //字符分割


        for (var k=0;k<cellRangeSplitArr.length ;k++ )
        {
            if     ("setRowVisible"==cellType)  //调用 setRowVisible 和 setColumnVisible 方法来指定行列是否可见。
            {
                sheet.setRowVisible(cellRangeSplitArr[k], false);
                continue;
            } else if("setColumnVisible"==cellType){
               sheet.setColumnVisible(stringTonum(cellRangeSplitArr[k])-1, false);
                continue;
            }

            var cellRangeSplitArr_SUB= new Array();
            cellRangeSplitArr_SUB=cellRangeSplitArr[k].split(":"); //字符分割,:代表的是范围

            var reg = /(\d+)$/g
            if(cellRangeSplitArr_SUB.length==2){
                // 判断范围
                var resultLow    = reg.exec(cellRangeSplitArr_SUB[0]);
                var resultHigh   = reg.exec(cellRangeSplitArr_SUB[1]);
                var matchNumLow  =cellRangeSplitArr_SUB[0].replace(/[^0-9]/ig,"");
                var matchNumHigh =cellRangeSplitArr_SUB[1].replace(/[^0-9]/ig,"");

                // 判断跨几列
                if(resultLow==null & resultHigh==null){
                 console.error("非预期-配置错误，应该配置为X:N格式字符串，必须指定具体的单元格起始位置："+cellRangeSplitArr_SUB[0]+"和终止位置坐标：" +cellRangeSplitArr_SUB[1])
                }
                // 判断跨几行 提取字符
                var matchStrLow  =cellRangeSplitArr_SUB[0].replace(/[^A-Z]/ig,"").replace(/[^a-z]/ig,"");
                var matchStrHigh =cellRangeSplitArr_SUB[1].replace(/[^A-Z]/ig,"").replace(/[^a-z]/ig,"");
                var rowL  = stringTonum(matchStrLow);
                var rowH =  stringTonum(matchStrHigh);

                for(var r= parseInt(rowL);r<= parseInt(rowH) ;r++){
                    for(var c=  parseInt(matchNumLow);c<= parseInt(matchNumHigh);c++){
                        CellArr.push(numToString(r)+c);
                    }
                }

            }else if(cellRangeSplitArr_SUB.length==1){
                //匹配正则获取小分组捕获结果
                var result = reg.exec(cellRangeSplitArr_SUB[0]);
                if(result){//如果没有匹配到，则result为null 证明这个是代表某一列，而不是某一个具体的单元格
                    // var matchNum = result[1];//结果数组中角标为1的值就是我们捕获的正则小分组中的串
                    // console.log(matchNum);
                    CellArr.push(cellRangeSplitArr_SUB[0]);
                }else{
                    //处理一列数据
                    var rowCount = sheet.getRowCount();
                    for(var m=0;m<rowCount;m++){
                        CellArr.push(cellRangeSplitArr_SUB[0]+m);
                    }
                }
            }else{
                console.error("非预期-配置错误，应该配置为X:N格式字符串，实际字符串为："+cellRangeSplitArr[k])
            }
        }

        for(var f=0;f<CellArr.length;f++ ){

                 var rowColumn=getIdFromColumnName(CellArr[f]);
                 if     ("Combo"==cellType){
                    var spreadNS = GC.Spread.Sheets;
                    var combo = new spreadNS.CellTypes.ComboBox();
                    combo.items(ItemsDataJson);
                    sheet.getCell(rowColumn[1], rowColumn[0], spreadNS.SheetArea.viewport).cellType(combo);
                 }else if("DatePicker"==cellType){
                    var notShowTimestyle = sheet.getStyle(rowColumn[1], rowColumn[0]);//new GC.Spread.Sheets.Style();
                     if(notShowTimestyle==null)
                     {
                         notShowTimestyle=  new GC.Spread.Sheets.Style()
                     }
                         notShowTimestyle.cellButtons = [
                             {
                                 imageType: GC.Spread.Sheets.ButtonImageType.dropdown,
                                 command: "openDateTimePicker",
                                 useButtonStyle: true,
                             }
                         ];
                         notShowTimestyle.dropDowns = [
                             {
                                 type: GC.Spread.Sheets.DropDownType.dateTimePicker,
                                 option: {
                                     showTime: false
                                 }
                             }];
                         sheet.setStyle(rowColumn[1], rowColumn[0], notShowTimestyle);

                }else if("CheckBoxList"==cellType){
                     var checkBoxList = new GC.Spread.Sheets.CellTypes.CheckBoxList();
                     checkBoxList.items(ItemsDataJson);
                     checkBoxList.direction(GC.Spread.Sheets.CellTypes.Direction.vertical);
                     checkBoxList.textAlign(GC.Spread.Sheets.CellTypes.TextAlign.right);
                     checkBoxList.maxColumnCount(2);
                     sheet.setCellType(rowColumn[1], rowColumn[0], checkBoxList, GC.Spread.Sheets.SheetArea.viewport);

                }else if("GroupList"==cellType){
                     var treeListData = {
                         multiSelect: false,
                         items: ItemsDataJson
                     };
                     var treeListStyle = sheet.getStyle(rowColumn[1], rowColumn[0]);
                     if(treeListStyle==null)
                     {
                         treeListStyle=  new GC.Spread.Sheets.Style()
                     }
                         treeListStyle.cellButtons = [
                             {
                                 imageType: GC.Spread.Sheets.ButtonImageType.dropdown,
                                 command: "openList",
                                 useButtonStyle: true,
                             }
                         ];
                         treeListStyle.dropDowns =[
                             {
                                 type: GC.Spread.Sheets.DropDownType.list,
                                 option: treeListData
                             }
                         ];
                         sheet.setStyle(rowColumn[1], rowColumn[0],treeListStyle);

                 }else if("UnLockCell"==cellType){//由于不能单独锁定某一个单元格，需要Sheet集中锁定，然后解锁指定单元格达到效果
                     var beforeStyle = sheet.getStyle(rowColumn[1], rowColumn[0]);
                     if(beforeStyle!=null){
                         beforeStyle.locked = false;
                         sheet.setStyle(rowColumn[1], rowColumn[0],beforeStyle);
                     }
                 }
                 else {
                    console.error("暂时未能实现的类型"+cellType)
                  }
        }
    }
    sheet.resumePaint();
}

function stringTonum(a) {
    var str = a.toLowerCase().split("");
    var al = str.length;
    var getCharNumber = function (charx) {
        return charx.charCodeAt() - 96;
    };
    var numout = 0;
    var charnum = 0;
    for (var i = 0; i < al; i++) {
        charnum = getCharNumber(str[i]);
        numout += charnum * Math.pow(26, al - i - 1);
    };
    return numout;
}

function numToString(numm) {
    var stringArray = [];
    var numToStringAction = function (nnum) {
        var num = nnum - 1;
        var a = parseInt(num / 26);
        var b = num % 26;
        stringArray.push(String.fromCharCode(64 + parseInt(b + 1)));
        if (a > 0) {
            numToStringAction(a);
        }
    }
    numToStringAction(numm);
    return stringArray.reverse().join("");
}
//console.log(getIdFromColumnName('C5')) => [2,4]
function getIdFromColumnName(id) {
    // Get the letters
    var t = /^[a-zA-Z]+/.exec(id)
    if (t) {
        // Base 26 calculation
        var code = 0
        for (var i = 0; i < t[0].length; i++) {
            code +=
                parseInt(t[0].charCodeAt(i) - 64) *
                Math.pow(26, t[0].length - 1 - i)
        }
        code--
        // Make sure  starts on zero
        if (code < 0) {
            code = 0
        }
        // Number
        var number = parseInt(/[0-9]+$/.exec(id))
        if (number > 0) {
            number--
        }
        id = [code, number]
    }
    // console.log(id)
    return id
}