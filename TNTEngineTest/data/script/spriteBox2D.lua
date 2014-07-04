BodyType = luajava.bindClass("com.badlogic.gdx.physics.box2d.BodyDef$BodyType")
Vector2 = luajava.bindClass("com.badlogic.gdx.math.Vector2")

local out = luajava.bindClass("java.lang.System").out

local function println(o)
	if o == nil then
		out:println("Nil value")
	else 
		out:println(o)
	end
end

local function ln()
	out:println("")
end

function fixtureDef(shape)
	ln()
	println("Init Fixture Def in Lua")
	local fixture = luajava.newInstance("com.tntstudio.box2d.LuaFixtureDef")
	fixture:setDensity(5.0)
	fixture:setRestitution(0.2)
	fixture:setShape(shape)
	println(fixture)
	println(shape)
	return fixture
end

function bodyDef(rx,ry)
	ln()
	println("Init Body Def in Lua")
	local def = luajava.newInstance("com.tntstudio.box2d.LuaBodyDef")
	def:setType(BodyType.DynamicBody)
	def.position:set(rx,ry) 
	println(rx)
	println(ry)
	println(def)
	return def
end


function shape(w,h)
	ln()
	println("Init Shape in Lua")
	local shape = luajava.newInstance("com.tntstudio.box2d.LuaPolygonShape")
	local vector2 = luajava.new(Vector2)
	vector2:set(-w/2,0)
	shape:setAsBox(w/2,h/2,vector2,0)
	println(w)
	println(h)
	println(shape)
	return shape
end

function attachFixture1(world,body,fixtureDef)
	ln()
	println("Attack Fixture in Lua")
	body:createFixture(fixtureDef)
	println(world)
	println(body)
	println(fixtureDef)
	return body
end

box2D = {
	initFixtureDef = fixtureDef,
	initBodyDef = bodyDef,
	initShape = shape,
	attachFixture = attackFixture1
}