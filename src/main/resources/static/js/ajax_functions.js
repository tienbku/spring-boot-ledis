$(document).ready(function() {

	$("#form").submit(function(event) {
		event.preventDefault();

		var cmd = $("#cmd").val();
		if (cmd.length > 0) {
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
					result.append(data.message + "<br>");
				},
				error : function(e) {
					result.append("(error) " + e.responseJSON.message + "<br>");
				},
				complete : function() {
					$("#btn-cmd").prop("disabled", false);
					$("#cmd").val("");

					result.animate({
						scrollTop : result.prop("scrollHeight")
					}, 0);
				}
			});
		}		
	});
});
