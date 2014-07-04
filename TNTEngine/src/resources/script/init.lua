-- Global info for further use
top = luajava.bindClass("com.tntstudio.core.Top")
tres = top.tres
tgame = top.tgame
tinp = top.tinp
tcus = top.tcus
tlua = top.tlua

BodyType = luajava.bindClass("com.badlogic.gdx.physics.box2d.BodyDef$BodyType")

-- store class for processing next

local fixtureDef_Class = luajava.bindClass("com.tntstudio.box2d.LuaFixtureDef")
local bodyDef_Class = luajava.bindClass("com.tntstudio.box2d.LuaBodyDef")
local polygonShape_Class = luajava.bindClass("com.tntstudio.box2d.LuaPolygonShape")
local vector2_Class = luajava.bindClass("com.badlogic.gdx.math.Vector2")

-- New instances methods

local function new_Vector2()
	return luajava.new(vector2_Class)
end

local function new_FixtureDef()
	return luajava.new(fixtureDef_Class)
end

local function new_BodyDef()
	return luajava.new(bodyDef_Class)
end

local function new_PolygonShape()
	return luajava.new(polygonShape_Class)
end

tnt = {
	newVector2 = new_Vector2,
	newFixtureDef = new_FixtureDef,
	newBodyDef = new_BodyDef,
	newPolygonShape = new_PolygonShape
}