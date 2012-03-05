function() { 
	db.http.session.find().forEach(
		function(obj) {
			if ((obj.lastAccessedTime + obj.maxInactiveInterval * 1000) < new Date().getTime()) 
				db.http.session.remove({_id: new ObjectId(obj._id.str)});
        }
	);
}