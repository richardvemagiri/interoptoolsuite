$(document).ready(function () {

    // $('.category-checkbox').on('click', function() {
    //     $('.category-checkbox').each(function() {
    //         if ($(this).prop("checked"))
    //             $("this").prop("checked", false);
    //         else
    //             $(this).prop("checked", true);
    //         })
    // });

    showFileUploadPage();
    //show default tab
    // showTextPage();
    // showFileForUserProfile();
    $('#testDiv').hide();
    // $('#deidentifybtn').fadeOut();
    $('#disclaimer').fadeIn();
    $('.category-checkbox').prop("checked", true);


    // check for browser support
    if (!(window.File && window.FileReader && window.FileList && window.Blob)) {
        $('#browserSupportWarn').addClass("alert-danger");
        $('#browserSupportWarn').html("<p style=\"color: red; text-align: center;\">Please not that certain features of the application may not be " +
            "fully supported due to browser limitations. Please upgrade your browser.</p>");
    }

    var myFileUploadModal = new bootstrap.Modal(document.getElementById('fileUploadModal'), {
        keyboard: false
    });
});


    $('#fileploadForm').submit(function (event) {

        var myFileUploadModal = new bootstrap.Modal(document.getElementById('fileUploadModal'), {
            keyboard: false
        });


        // check if file is picked
        if ($('#fileUpload').val().length === 0) {
            // $('#exampleModal1').load("modalFeedBack");

            // var modalfeedBack = $($.parseHTML(html)).filter("#exampleModal");
            $('#fileUploadModalContent').html("Please select a file!");
            myFileUploadModal.show();
            // myModal.html(modalfeedBack);
            return false;
        }

        // check if file is XML
        var allowedExtensions = /(\.xml|\.XML)$/i;
        if (!allowedExtensions.exec($('#fileUpload').val())) {
            $('#fileUploadModalContent').html("Please select a valid C-CDA XML file!");
            myFileUploadModal.show();
            // alert('Please select a valid C-CDA XML file');
            return false;
        }

        // check if file is too big
        // var maxFileSize = 5242880;
        // if ($('#fileUpload')[0].files[0].size > maxFileSize) {
        //     alert("File is larger than " + Math.round(maxFileSize / 1000000) + " MB");
        //     return false;
        // }

        // check if checkboxes are selected
        let identifierTypes = [];
        $(".category-checkbox:checked").each(function() {
            identifierTypes.push($(this).val());
        });
        if(identifierTypes.length<=0){
            alert("No identifier categories selected!");
            return false;
        }

        $('#deidentifybtn').removeAttr("data-bs-target");
        event.preventDefault();
        submitFile();
    });


function timeout(ms){
    console.log("Inside timeout ...");
    return new Promise(res => setTimeout(res, ms));
}

function showTextPage(){

    $('#texttab').attr('aria-current', 'true');
    $('#texttab').addClass('active fw-bolder');
    $('#fileuploadtab').removeAttr('aria-current');
    $('#fileuploadtab').removeClass('active fw-bolder');
    $('#textmodepage').show();
    $('#fileuploadpage').hide();
    $('#text-progress-holder').css("visibility", "hidden");
}

function showFileUploadPage(){
    // alert("FileUploadPage");
    $('#fileuploadtab').attr('aria-current', 'true');
    $('#fileuploadtab').addClass('active fw-bolder');
    $('#texttab').removeAttr('aria-current');
    $('#texttab').removeClass('active fw-bolder');
    // $('#fileUploadContainer').addClass('d-block');
    $('#textmodepage').hide();
    $('#fileuploadpage').show();
}

function submitFile() {
    console.log("File submitted for processing...");

    var data = new FormData();
    data.append('file', $('#fileUpload')[0].files[0]);

    let arr = [];
    $(".category-checkbox:checked").each(function() {
        arr.push($(this).val());
    });

    // alert($('input[name="category"]:checked').val());

    data.append('categories', arr);

    var progressBar = $('#progress_bar');
    // progressBar.text('0%');
    progressBar.attr('aria-valuenow', 0);


    progressBar.removeClass('d-none');
    progressBar.css("display","");

    progressBar.attr('aria-valuenow', 10);
    progressBar.css('width', 10 + '%');
    // progressBar.fadeIn();

    $('#progress-holder').css("visibility", "visible");

    $('#download').children().remove();

    $.ajax({
        xhr: function () {
            var xhr = new window.XMLHttpRequest();
            xhr.upload.addEventListener("progress", function (evt) {
                if (evt.lengthComputable) {
                    let percentComplete = 0;
                    percentComplete = (evt.loaded / evt.total) * 80;
                    // Place progress bar visibility code here

                    console.log("Upload:", percentComplete)
                    // progressBar.text(percentComplete + '%');
                    progressBar.attr('aria-valuenow', percentComplete);
                    progressBar.css('width', percentComplete + '%');
                    // progressBar.val(percentComplete);

                }
            }, false);
            return xhr;
        },
        method: "POST",
        url: "/deid-tool/",
        contentType: false,
        data: data,
        cache: false,
        processData: false,
        timeout: 60000,
        success:  function (response) {
            progressBar.css('width', 100 + '%');

            setTimeout(function () {
                progressBar.fadeOut('fast', function () {
                    progressBar.addClass('d-none');
                    $('#progress-holder').css("visibility", "hidden");

                });
            }, 0);

            setTimeout(() => {
                // Code to be executed after 1 second
                // $('.deidentifybtn').prop('disabled', true);
                var usrfeedback = $($.parseHTML(response)).filter("#feedback");
                console.log(usrfeedback);
                $('#testDiv').show();
                $('#testDiv').html(usrfeedback);
                console.log("Before await timeout");
                // await timeout(500);
                console.log("After await timeout");

                $('#download').html(response).fadeIn(3000);
                setTimeout(function () {
                    $("#feedback").fadeOut(3000);
                }, 10000);
            }, 500);


        },

        error: function (xhr, response) {

            progressBar.css('width', 0 + '%');
            setTimeout(function () {
                progressBar.fadeOut('fast', function () {
                    progressBar.addClass('d-none');
                    $('#progress-holder').css("visibility", "hidden");

                });
            }, 0);

            if(xhr.status == 413){
                alert("Error occured: File too large");
                return false;
            }

            var myFileUploadModal = new bootstrap.Modal(document.getElementById('fileUploadModal'), {
                keyboard: false
            });
            // alert('Error occurred! Please refresh the page and try again!');
            $('#fileUploadModalContent').html("Error occurred! <br/>Please refresh the page and try again.");
            myFileUploadModal.show();
            var usrfeedback = $($.parseHTML(response)).filter("#feedback");
            $('#testDiv').show();
            $('#testDiv').html(usrfeedback);

            // $('#download').html(response).fadeIn(3000);
            // setTimeout(function () {
            //     $("#feedback").fadeOut(3000);
            // }, 10000);
        }
    });
}
function showFileForUserProfile() {

    $.ajax({
        method: "GET",
        url: "/deid-tool/loadFileForUser",
        contentType: false,
        cache: false,
        processData: false,
        timeout: 600000,
        success: async function (response) {
            // $('.deidentifybtn').prop('disabled', true);
            var usrfeedback = $($.parseHTML(response)).filter("#feedback");
            console.log(usrfeedback);
            $('#testDiv').show();
            $('#testDiv').html(usrfeedback);
            console.log("Before await timeout");
            await timeout(500);
            console.log("After await timeout");
            $('#download').html(response).fadeIn(100);
            setTimeout(function () {
                $("#feedback").fadeOut(10000);
            }, 1000);

        },
        error: function (response) {
            alert('Error occurred! Please try again!');
            // var usrfeedback = $($.parseHTML(response)).filter("#feedback");
            // $('#outputText').show();
            $('#outputText').html(response);
        }
    });
}
