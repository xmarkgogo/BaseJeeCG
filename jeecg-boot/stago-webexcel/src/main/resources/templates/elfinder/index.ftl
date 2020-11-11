<#assign base=springMacroRequestContext.getContextUrl("")>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=2">
		<title>elFinder 2.1.x source version with PHP connector</title>

		<!-- Section CSS -->
		<!-- jQuery UI (REQUIRED) -->
		<link rel="stylesheet" type="text/css" href="${base}/elfinder/js/jquery1.12.1/jquery-ui.css">

		<!-- elFinder CSS (REQUIRED) -->
		<link rel="stylesheet" type="text/css" href="${base}/elfinder/css/elfinder.min.css">
		<link rel="stylesheet" type="text/css" href="${base}/elfinder/css/theme.css">

		<!-- Section JavaScript -->
		<!-- jQuery and jQuery UI (REQUIRED) -->
		<!--[if lt IE 9]>
		<script src="${base}/elfinder/js/jquery1.12.1/jqueryie9.min.js"></script>
		<![endif]-->
		<!--[if gte IE 9]><!-->
		<script src="${base}/elfinder/js/jquery1.12.1/jquery.min.js"></script>
		<!--<![endif]-->
		<script src="${base}/elfinder/js/jquery1.12.1//jquery-ui.min.js"></script>

		<!-- elFinder JS (REQUIRED) -->
		<script src="${base}/elfinder/js/elfinder.min.js"></script>

		<!-- Extra contents editors (OPTIONAL) -->
		<script src="${base}/elfinder/js/extras/editors.default.min.js"></script>

		<!-- GoogleDocs Quicklook plugin for GoogleDrive Volume (OPTIONAL) -->
		<!--<script src="js/extras/quicklook.googledocs.js"></script>-->

		<!-- elFinder initialization (REQUIRED) -->
		<script type="text/javascript" charset="utf-8">
			// Documentation for client options:
			// https://github.com/Studio-42/elFinder/wiki/Client-configuration-options
			$(document).ready(function() {
				$('#elfinder').elfinder(
					// 1st Arg - options
					{
						//'themes/windows-10/css/theme.css', 如果恢复默认注释掉autolaod
						cssAutoLoad : ['themes/Material/css/theme-light.css'],
						//cssAutoLoad : false,               // Disable CSS auto loading
						baseUrl : './',                    // Base URL to css/*, js/*
                        url : '${base}/elfinder-servlet/connector',  // connector URL (REQUIRED)
                        lang: 'zh_CN',
                        height : window.innerHeight-20,
                        commands : [
                            'WebExcel','open', 'reload', 'home', 'up', 'back', 'forward', 'getfile', 'quicklook',
                            'download', 'rm', 'duplicate', 'rename', 'mkdir', 'mkfile', 'upload', 'copy',
                            'cut', 'paste', 'edit', 'extract', 'archive', 'search', 'info', 'view', 'help', 'resize', 'sort', 'netmount'
                        ],
                        contextmenu : {
                            // navbarfolder menu
                            navbar : ['open', '|', 'copy', 'cut', 'paste', 'duplicate', '|', 'rm', '|', 'info'],
                            // current directory menu
                            cwd    : ['reload', 'back', '|', 'upload', 'mkdir', 'mkfile', 'paste', '|', 'sort', '|', 'info'],
                            // current directory file menu
                            files  : ['getfile', '|', 'WebExcel', 'quicklook', '|', 'download', '|', 'copy', 'cut', 'paste', 'duplicate', '|', 'rm', '|', 'edit', 'rename', 'resize', '|', 'archive', 'extract', '|', 'info']
                        },
					},
					// 2nd Arg - before boot up function
					function(fm, extraObj) {
						// `init` event callback function
						fm.bind('init', function() {
							// Optional for Japanese decoder "encoding-japanese.js"
							if (fm.lang === 'ja') {
								fm.loadScript(
									[ '//cdn.rawgit.com/polygonplanet/encoding.js/1.0.26/encoding.min.js' ],
									function() {
										if (window.Encoding && Encoding.convert) {
											fm.registRawStringDecoder(function(s) {
												return Encoding.convert(s, {to:'UNICODE',type:'string'});
											});
										}
									},
									{ loadType: 'tag' }
								);
							}
						});
						// Optional for set document.title dynamically.
						var title = document.title;
						fm.bind('open', function() {
							var path = '',
								cwd  = fm.cwd();
							if (cwd) {
								path = fm.path(cwd.hash) || null;
							}
							document.title = path? path + ':' + title : title;
						}).bind('destroy', function() {
							document.title = title;
						});
					}
				);
			});

            elFinder.prototype.commands.WebExcel= function() {
                this.exec = function(hashes) {
                    //implement what the custom command should do here
                    var file = this.files(hashes);
                    var hash = file[0].hash;
                    var host = window.location.host;
                    var protocol=document.location.protocol;
                   // var ActionURL=protocol+"//"+host+"/spreadjs/SpreadJS.Designer.13.1.0/index/index.html?cmd=WebExcel"+"&target="+hash;
					var ActionURL="${base}/spreadjs/SpreadJS.Designer.13.1.0/index/index.html?cmd=WebExcel"+"&target="+hash;
                    window.open(ActionURL,'WebExcel'+hash,"fullscreen=0,directories=1,location=1,menubar=1,resizable=1,scrollbars=1,status=1,titlebar=1,toolbar=1");
                    //var fm = this.fm;
                    // console.info(fm);
                    // var url = fm.url(hash);
                    //alert(ActionURL);
                    //var absoluteUrl = fm.convAbsUrl(url);
                    //alert(absoluteUrl);
                }
                this.getstate = function() {
                    //return 0 to enable, -1 to disable icon access
                    var sel = this.files(sel),
                        cnt = sel.length;
                    if(cnt>1)return -1;
                    if(cnt==1){
                        var file=sel[0];
                        console.info(sel);
                        console.info(file.name);
                        console.info(file.mime);
                        if(file.name.lastIndexOf(".xlsx")>0 | file.name.lastIndexOf(".isqd")>=0 | file.name.lastIndexOf(".JSON")>=0 ){
                            return 0;
                        }else{
                            return -1;
                        }

                    }
                    return !this._disabled && cnt ? 0 : -1;
                }
            }
		</script>
	</head>
	<body>

		<!-- Element where elFinder will be created (REQUIRED) -->
		<div id="elfinder"></div>

	</body>
</html>