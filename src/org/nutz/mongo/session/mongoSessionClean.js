function() { 
	var now = new Date().getTime();
	db.http.session.find().snapshot().forEach(
		function(obj) {
			if ((obj.lastAccessedTime + obj.maxInactiveInterval * 1000) < now )  {
				//print('Session removed : '+ obj._id.str);
				db.http.session.remove({_id: new ObjectId(obj._id.str)});
			}
        }
	);
	//print('Session Clean complete!');
}