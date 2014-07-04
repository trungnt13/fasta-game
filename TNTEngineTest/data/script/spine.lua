out = luajava.bindClass("java.lang.System").out

function spineWalk(sprite,delta)
	sprite:setAnimation(1,"walk",true)
	sprite:setAnimation(2,"drawOrder",true)
	sprite:setPlaybackSpeed(1):start(0.0)
	out:println("Spine Walk Called from Lua")
	return true
end

function spineJump(sprite,delta)
	sprite:setAnimation(1,"jump",true)
	sprite:setAnimation(2,"drawOrder",true)
	sprite:setPlaybackSpeed(1):start(0.0)
	out:println("Spine Jump Called from Lua")
	return true
end

walk = {
	update = spineWalk
}

jump = {
	update = spineJump
}