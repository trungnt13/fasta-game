	do
		sys = luajava.bindClass("java.lang.System")
	end
	
	function prin()
		print("Shit over here")
	end

	
	function println(obj)
		print(obj)
		sys.out:println(obj)
	end