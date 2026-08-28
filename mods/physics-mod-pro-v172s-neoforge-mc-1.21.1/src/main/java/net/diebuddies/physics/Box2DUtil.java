/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.physics;

import net.diebuddies.jbox2d.collision.shapes.PolygonShape;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.BodyDef;
import net.diebuddies.jbox2d.dynamics.BodyType;
import net.diebuddies.jbox2d.dynamics.Fixture;
import net.diebuddies.jbox2d.dynamics.FixtureDef;
import net.diebuddies.jbox2d.dynamics.World;

public class Box2DUtil {
    public static Body createBox(World world, float x, float y, float width, float height, BodyType type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(x + width / 2.0f, y + height / 2.0f);
        bodyDef.angle = 0.0f;
        Body body = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2.0f, height / 2.0f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        Fixture fixture = body.createFixture(fixtureDef);
        return body;
    }
}

