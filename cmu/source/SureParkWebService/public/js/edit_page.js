//우선순위 막대 
function showVal2(id,newVal){
    $('#'+id+"_result").text("우선순위 : "+newVal);
}

function updatePageList(arg,callback)
{
    if(arg === undefined){
        arg = {};
        arg.query = {};
    }
    setUserName(userName, userType);
    var keys = ['title','description','url','image_path','expose','pl','priority','button'];

    var val = {title:'TITLE', description:'DESCRIPTION',url:'URL',
        image_path:'IMAGE', expose:'개발자',pl:'관리자',priority:'우선순위', button:'삭제/확인'};

    var list_page=[];
    $.get('page_list', function(data){
        var edit_pageList = $('#page_list');
        edit_pageList.empty();
        var table = $('<table></table>').appendTo(edit_pageList);

        var tr = $('<tr></tr>').appendTo(table);
        keys.forEach(function(key){
            $('<th>'+val[key]+'</th>').addClass(key).appendTo(tr);
        });

        data.forEach(function(item){
            console.log(item);
            var tr = $('<tr></tr>').appendTo(table);

            keys.forEach(function(key){
                var td = $('<td></td>').addClass(key).appendTo(tr);
                if(key === 'button')
                {
                    var deleteBtn = $('<span></span>').attr('data-dbid',item['_id']).addClass('deleteBtn').click(function(){
                        var dbid = $(this).attr('data-dbid');
                        removePage(dbid);
                    });;
                    $('<span class="registerBtn2"><i class="fa fa-trash"></i></span>').appendTo(deleteBtn);
                    deleteBtn.appendTo(td);

                    var editBtn = $('<span class="registerBtn2"><i class="fa fa-check"></i></span>').attr('data-dbid',item['_id']).click(function(){
                        var obj={};
                        keys.forEach(function(key_item){      
                            var tmp = $("#"+item['_id']+"_"+key_item);
                            if(key_item == 'expose'){
                                obj[key_item]=tmp.is(":checked");
                            }else if(key_item == 'pl'){
                                obj[key_item]=tmp.is(":checked");
                            }else if (key_item == 'image_path'){
                                obj[key_item]=tmp.attr('src');
                            }else{
                                obj[key_item]=tmp.val();
                            }
                        });
                        $.post('updatePage',{_id:item['_id'], obj: obj},function(data){
                            if(data.result === 'error'){
                                alert(data.errorMsg);
                            }
                            else{
                                location.reload("/edit");
                            }
                        });
                    });
                    editBtn.appendTo(td);                   
                }else if(key === 'image_path'){
                    var img=$('<img src="'+item[key]+'" style="width:180px; height:115px">').attr('id',item['_id']+"_"+key);
                    img.appendTo(td);
                    var form = $('<form method="post" name="formUpload" id="formUpload"></form>').appendTo(td);
                    form.submit(function(event){        
                        //disable the default form submission
                        event.preventDefault();

                        var fd = new FormData($(this)[0]);  
                        $.ajax({
                            url: "/upload",
                            type: "POST",
                            data: fd, 
                            async: false,
                            cache: false,
                            contentType: false,
                            processData: false,
                            success:  function(data){
                                console.log(data.filename);
                                //이미지 바꾸고 attr에 filename 넣기
                                img.attr("src",data.filename);
                                alert("이미지가 업로드 되었습니다");
                            }
                        });
                        
                        return false;
                        
                    });
                    $('<input type="file" name="uploadFile" id="pageImage"><input type="submit" class="mBtn_upload" value="업로드">').appendTo(form);
                }else if(key === 'expose'){
                    if(item[key]==true){
                        var expose=$('<input type="checkbox" checked></>');
                        expose.attr('id',item['_id']+"_"+key).attr('name',key).attr('value',key);
                        expose.appendTo(td);
                    }else{
                        var expose=$('<input type="checkbox"></>');
                        expose.attr('id',item['_id']+"_"+key).attr('name',key).attr('value',key);
                        expose.appendTo(td);
                    }                    
                }else if(key === 'pl'){
                    if(item[key]==true){
                        var expose=$('<input type="checkbox" checked></>');
                        expose.attr('id',item['_id']+"_"+key).attr('name',key).attr('value',key);
                        expose.appendTo(td);
                    }else{
                        var expose=$('<input type="checkbox"></>');
                        expose.attr('id',item['_id']+"_"+key).attr('name',key).attr('value',key);
                        expose.appendTo(td);
                    }                    
                }else if(key === 'url'){
                    $('<input type="url" value="'+item[key]+'">').attr('name',key).attr('id',item['_id']+"_"+key)
                    .appendTo(td);
                }else if(key === 'priority'){
                    var first = $('<input type="range" value="'+item[key]+'"min="0" max="60" oninput="showVal2(this.id, this.value)" >')
                    .attr('id',item['_id']+"_"+key).attr('name',key).appendTo(td);
                    var second = $('<p></p>').attr('id',item['_id']+"_priority_result").appendTo(td);
                    second.text("우선순위 : "+first.val());
                }else{
                    if(item[key] === undefined){
                        item[key] = '';
                    }
                    $('<input type="text" value="'+item[key]+'">').attr('name',key).attr('id',item['_id']+"_"+key)
                    .appendTo(td);
                }                
        });        
    });
    
    if(typeof callback !== 'undefined')
        callback();
    });
}

updatePageList();

function removePage(dbid)
{
    $("#dialog-confirm").dialog({
        modal: true,
        buttons: {
            "삭제": function() {
                $.get("removePage", {_id:dbid} ,function(data){
                    if(data.result === 'error'){
                        alert(data.errorMsg);
                    }
                    else{
                        //updateWorkList();
                       location.reload("/edit");
                    }
                });
                $( this ).dialog( "close" );
            },
            "취소": function() {
            $( this ).dialog( "close" );
            }
        }
    });
}
