'use strict';

var singleUploadForm = document.querySelector('#singleUploadForm');
var singleFileUploadInput = document.querySelector('#singleFileUploadInput');
var singleFileUploadError = document.querySelector('#singleFileUploadError');
var singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');

function uploadSingleFile(file) {
	var formData = new FormData();
	formData.append("file", file);

	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/uploadFile");

	xhr.onload = function() {
		console.log(xhr.responseText);
		var response = JSON.parse(xhr.responseText);
		if (xhr.status == 200) {
			singleFileUploadError.style.display = "none";
			singleFileUploadSuccess.innerHTML = "<p>File Uploaded Successfully.</p><p>DownloadUrl : <a href='" + response.fileDownloadUri + "' target='_blank'>" + response.fileDownloadUri + "</a></p>";
			singleFileUploadSuccess.style.display = "block";
		} else {
			singleFileUploadSuccess.style.display = "none";
			singleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
		}
	}

	xhr.send(formData);
}

singleUploadForm.addEventListener('submit', function(event) {
	var files = singleFileUploadInput.files;
	if (files.length === 0) {
		singleFileUploadError.innerHTML = "Please select a file";
		singleFileUploadError.style.display = "block";
	}
	uploadSingleFile(files[0]);
	event.preventDefault();
}, true);

$('#multipleUploadForm').submit(function(event) {
	var formElement = this;
	var formData = new FormData(formElement);

	var files = $('#multipleFileUploadInput').prop('files');
	if (files.length === 0) {
		$('#multipleFileUploadError').innerHTML = "Please select at least one file";
		$('#multipleFileUploadError').style.display = "block";
	}
	$.ajax({
		type: "POST",
		enctype: 'multipart/form-data',
		url: "/uploadMultipleFiles",
		data: formData,
		processData: false,
		contentType: false,
		success: function(response) {
			console.log(response);
			$('#multipleFileUploadError').style.display = "none";
			var content = "<p>All Files Uploaded Successfully</p>";
			for (var i = 0; i < response.length; i++) {
				content += "<p>DownloadUrl : <a href='" + response[i].fileDownloadUri + "' target='_blank'>" + response[i].fileDownloadUri + "</a></p>";
			}
			$('#multipleFileUploadSuccess').innerHTML = content;
			$('#multipleFileUploadSuccess').style.display = "block";
		},
		error: function(error) {
			console.log(error);
			$('#multipleFileUploadSuccess').style.display = "none";
			$('#multipleFileUploadError').innerHTML = (response && response.message) || "Some Error Occurred";
		}
	});

	event.preventDefault();
});