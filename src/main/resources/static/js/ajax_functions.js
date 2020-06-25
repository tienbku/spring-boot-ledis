$(document).ready(function() {

	$("#form").submit(function(event) {
		event.preventDefault();
	
		var cmd = $("#cmd").val();
		
		var req = {}
		req["command"] = cmd;
		
		var result = $("#result");
		result.append(">" + cmd + "<br>");

		$("#btn-cmd").prop("disabled", true);
		

		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "/api/ledis",
			data : JSON.stringify(req),
			dataType : 'json',
			cache : false,
			timeout : 600000,
			success : function(data) {
				console.log("SUCCESS : ", data);

				result.append(data.command + "<br>");
				$("#btn-cmd").prop("disabled", false);
				result.animate({
					scrollTop : result.prop("scrollHeight")
				}, 0);
			},
			error : function(e) {
				console.log("ERROR : ", e);
				$("#btn-cmd").prop("disabled", false);
				result.animate({
					scrollTop : result.prop("scrollHeight")
				}, 0);
			}
		});
	});
});
