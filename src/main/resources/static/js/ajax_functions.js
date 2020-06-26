$(document).ready(function() {

	$("#form").submit(function(event) {
		event.preventDefault();
	
		var cmd = $("#cmd").val();
			
		var result = $("#result");
		result.append(">" + cmd + "<br>");

		$("#btn-cmd").prop("disabled", true);
		

		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "/api/ledis",
			data : cmd,
			dataType : 'json',
			cache : false,
			timeout : 600000,
			success : function(data, textStatus, xhr) {
				console.log("SUCCESS : ", data);
				
				if (xhr.status == 204) {
					result.append("(nil)<br>");
				} else {
					result.append(data.message + "<br>");
				}
				
				$("#btn-cmd").prop("disabled", false);
				$("#cmd").val("");
				
				result.animate({
					scrollTop : result.prop("scrollHeight")
				}, 0);
			},
			error : function(e) {
				console.log("ERROR : ", e);
				
				result.append("(error) " + e.responseJSON.message + "<br>");
				
				$("#btn-cmd").prop("disabled", false);
				$("#cmd").val("");
				
				result.animate({
					scrollTop : result.prop("scrollHeight")
				}, 0);
			}
		});
	});
});
