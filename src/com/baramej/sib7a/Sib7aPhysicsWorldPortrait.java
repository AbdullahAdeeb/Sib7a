package com.baramej.sib7a;

import java.util.ArrayList;

import org.andengine.engine.FixedStepEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleLayoutGameActivity;

import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga
 * 
 * @author Nicolas Gramlich
 * @since 18:47:08 - 19.03.2010
 */
public class Sib7aPhysicsWorldPortrait extends SimpleLayoutGameActivity implements IAccelerationListener, IOnSceneTouchListener {

	// ===========================================================
	// Constants
	// ===========================================================

	protected int CAMERA_HEIGHT = 836;
	protected int CAMERA_WIDTH = 720;
	private static final String BEAD_NAME_PREFIX = "a";
	private static final String BACKGROUND_ASSET_NAME = "black_abstract_background.png";
	private static final String TOP_SHADE_BACKGROUND_PNG = "top_shade_background.png";
	private static final String TOP_SHADE_SHADOW_PNG = "top_shade_shadow.png";
	private static final String TOP_SHADE_PNG = "top_shade.png";
	// ===========================================================
	// Fields
	// ===========================================================

	private Scene mScene;
	private BuildableBitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mBackgroundRegion;
	private ArrayList<ITextureRegion> mBeanTextureRegions;
	private ITextureRegion topShadeRegion;
	protected PhysicsWorld mPhysicsWorld;

	private SharedPreferences internalPrefs;
	// ArrayList<Body> beadBodyArray;
	ArrayList<Sprite> beadSpritesArray;
	private Sprite topShade;
	private int numberOfBeads;
	private PulleyJointDef topPulleyJoint;
	private Scene coverScene;
	private TextureRegion topShadeShadowRegion;
	private TextureRegion topShadeBackgroundRegion;
	private Rectangle mRoof;
	protected boolean isCoverTouched;
	private TextureRegion arrowUpRegion;
	private Sprite arrowUpSprite;
	private boolean isMainTouched;

	// ===========================================================
	// Constructors
	// ===========================================================
	// Sib7aPhysicsWorld2(int cameraWidth,int cameraHeight){
	// this.CAMERA_WIDTH = cameraWidth;
	// this.CAMERA_HEIGHT = cameraHeight;
	//
	// }
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected int getLayoutID() {
		return R.layout.activity_sib7a;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.physicsWorldFrameContainer;
	}

	@Override
	public EngineOptions onCreateEngineOptions() {

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		// return new EngineOptions(true,
		// ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(),
		// camera);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
				new RatioResolutionPolicy(this.CAMERA_WIDTH, this.CAMERA_HEIGHT), camera);
		return engineOptions;
	}

	@Override
	public void onCreateResources() {

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(mEngine.getTextureManager(), 2000, 2000,
				BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
		
		this.mBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this,
				BACKGROUND_ASSET_NAME);
		loadBeads();
		this.topShadeRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this,
				TOP_SHADE_PNG);
		this.topShadeShadowRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas,
				this, TOP_SHADE_SHADOW_PNG);
		this.topShadeBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas,
				this, TOP_SHADE_BACKGROUND_PNG);
		this.arrowUpRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this,
				"arrow_up.png");

		try {
			this.mBitmapTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0,
							0, 0));
		} catch (TextureAtlasBuilderException e) {
			// TODO handle assets load error
			System.out.println("OPS! something went wrong at 451");
			e.printStackTrace();
		}
		this.mBitmapTextureAtlas.load();

		this.internalPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	void loadBeads(){
		this.mBeanTextureRegions = new ArrayList<ITextureRegion>();
		for (int i = 1; i <= 5; i++) {
			this.mBeanTextureRegions.add(BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this,
					"Beads/"+BEAD_NAME_PREFIX+i+".png"));
		}
		
	}
	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.coverScene = new Scene();
		this.mScene.setChildScene(this.coverScene);

		this.coverScene.setBackgroundEnabled(false);
		Sprite backgroundSprite = new Sprite(0, 0, this.mBackgroundRegion, mEngine.getVertexBufferObjectManager());
		this.mScene.setBackground(new SpriteBackground(backgroundSprite));

		this.mScene.setOnSceneTouchListener(this);
		this.coverScene.setOnSceneTouchListener(new CoverSceneTouchHandler());
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		this.topShade = new Sprite(0, -2, this.topShadeRegion, this.getVertexBufferObjectManager());
		Sprite topShadeShadow = new Sprite(0, topShade.getY() + topShade.getHeightScaled(), this.topShadeShadowRegion,
				mEngine.getVertexBufferObjectManager());
		Sprite topShadeBackground = new Sprite(0, 0, topShadeBackgroundRegion, mEngine.getVertexBufferObjectManager());
		topShadeBackground.setPosition(0, topShadeShadow.getY() - topShadeBackground.getHeightScaled() - 3);
		this.arrowUpSprite = new Sprite(0, 0, this.arrowUpRegion, mEngine.getVertexBufferObjectManager());
		arrowUpSprite.setPosition((CAMERA_WIDTH) - (arrowUpSprite.getWidthScaled()), getCoverTopPosition()
				- arrowUpSprite.getHeightScaled());

		scaleToFillWidth(backgroundSprite);
		scaleToFillWidth(topShade);
		scaleToFillWidth(topShadeShadow);
		scaleToFillWidth(topShadeBackground);

		ITextureRegion mBeanTextureRegion = mBeanTextureRegions.get(0);
		final Rectangle top = new Rectangle(0, topShade.getHeightScaled(), CAMERA_WIDTH, 0, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 0, CAMERA_HEIGHT * 2, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH, 0, 0, CAMERA_HEIGHT * 2, vertexBufferObjectManager);
		final Rectangle innerBox = new Rectangle(CAMERA_WIDTH * 0.25f + (mBeanTextureRegion.getWidth() * 1.2f / 2),
				topShade.getHeightScaled(), CAMERA_WIDTH * 0.5f - (mBeanTextureRegion.getWidth() * 1.2f),
				CAMERA_HEIGHT / 3, vertexBufferObjectManager);
		final Rectangle outterLeft = new Rectangle(0, topShade.getHeightScaled(), CAMERA_WIDTH * 0.25f
				- (mBeanTextureRegion.getWidth() * 1.2f / 2), CAMERA_HEIGHT * 0.35f, vertexBufferObjectManager);
		final Rectangle outterRight = new Rectangle(CAMERA_WIDTH * 0.75f + (mBeanTextureRegion.getWidth() * 1.2f / 2),
				topShade.getHeightScaled(), CAMERA_WIDTH * 0.25f - (mBeanTextureRegion.getWidth() * 1.2f / 2),
				CAMERA_HEIGHT * 0.35f, vertexBufferObjectManager);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(100, 0f, 0f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, top, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, innerBox, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, outterLeft, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, outterRight, BodyType.StaticBody, wallFixtureDef);

		this.mRoof = new Rectangle(0, 0, CAMERA_WIDTH, 5, vertexBufferObjectManager);
		Body mRoofBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, mRoof, BodyType.KinematicBody,
				PhysicsFactory.createFixtureDef(0, 0, 0, false, (short) 0, (short) 0, (short) 1));

		mRoof.setVisible(false);// TODO FALSE
		innerBox.setVisible(false);
		outterLeft.setVisible(false);
		outterRight.setVisible(false);

		// top.setColor(0, 1, 0);

		this.mScene.attachChild(top);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
		this.mScene.attachChild(innerBox);
		this.mScene.attachChild(outterLeft);
		this.mScene.attachChild(outterRight);
		this.mScene.attachChild(mRoof);

		createBeadChain();
		attachBeadChainByPulleyJoint();
		this.coverScene.attachChild(topShadeBackground);
		this.coverScene.attachChild(topShade);
		this.coverScene.attachChild(topShadeShadow);
		this.coverScene.attachChild(arrowUpSprite);

		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mRoof, mRoofBody) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				coverScene.setPosition(0,
						(this.mBody.getPosition().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT)
								- topShade.getHeightScaled());

				if (!isCoverTouched) {
					if (getCoverRoofPosition() < 600) {
						/* should open */
						if (getCoverRoofPosition() < 0) {
							this.mBody.setLinearVelocity(0, 0);
							this.mBody.setTransform(this.mBody.getWorldCenter().x, 0, 0);
						}
						this.mBody.setLinearVelocity(0, -getCoverTopPosition());
					} else if (getCoverRoofPosition() > 600) {
						/* should close */
						if (getCoverRoofPosition() > CAMERA_HEIGHT) {
							// setCoverRoofPosition(CAMERA_HEIGHT);
							this.mBody.setTransform(this.mBody.getWorldCenter().x, CAMERA_HEIGHT
									/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
							this.mBody.setLinearVelocity(0, 0);
							return;
						}
						this.mBody.setLinearVelocity(0, CAMERA_HEIGHT - getCoverRoofPosition());
					}
				} else {
					this.mBody.setLinearVelocity(0, 0);
				}

				/*
				 * to change the bottom alpha boundry, change
				 * the constant at the end to change the upper
				 * boundry, change the first constant
				 */
				float alpha = (getCoverRoofPosition() - (CAMERA_HEIGHT * 0.7f)) / (CAMERA_HEIGHT * 0.3f);
				if (alpha < 0)
					alpha = 0;
				arrowUpSprite.setAlpha(alpha);

			}
		});

		setCoverRoofPosition(CAMERA_HEIGHT);
		return this.mScene;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		// System.out.println(">>>> mScene touch detected");
		isMainTouched = true;
		if (this.beadSpritesArray.size() > 0) {
			Body beadBody;
			if (pSceneTouchEvent.getX() <= CAMERA_WIDTH / 2) {
				beadBody = mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(beadSpritesArray.get(0));
			} else {
				beadBody = mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(
						beadSpritesArray.get(beadSpritesArray.size() - 1));
			}

			int y = (int) (pSceneTouchEvent.getY() - (beadBody.getPosition().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT));
			beadBody.setLinearVelocity(0, y);

		}
		if (pSceneTouchEvent.isActionUp()) {
			isMainTouched = false;
		}
		return true;
	}

	@Override
	public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {

	}

	@Override
	public void onAccelerationChanged(final AccelerationData pAccelerationData) {
		// TODO add Math.abs() to the Y vector
		final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
	}

	/*
	 * /////////////////////////////////////////////////////// MY METHODS
	 */// ///////////////////////////////////////////////

	// ===========================================================
	// Methods
	// ===========================================================

	private void createBeadChain() {
		float pFirstX = CAMERA_WIDTH * 0.25f;
		float pFirstY = topShade.getHeightScaled();
		this.beadSpritesArray = new ArrayList<Sprite>();

		final Sprite bFirstSprite = new Sprite(pFirstX, pFirstY, this.mBeanTextureRegions.get(0),
				this.getVertexBufferObjectManager());
		final Body bFirstBody = createBeadBody(bFirstSprite);
		// bFirstBody.setTransform(pFirstX -
		// bFirstSprite.getWidthScaled(), pFirstY, 0);
		// anchorBodyA.setTransform(anchorBodyA.getWorldCenter(),
		// (float) (Math.PI * 0.5));
		beadSpritesArray.add(bFirstSprite);
		mPhysicsWorld.registerPhysicsConnector(new BeadPhysicsConnector(bFirstBody, bFirstSprite));

		Sprite bLastSprite = bFirstSprite;
		Body bLastBody = bFirstBody;
		// calculate beads required
		int verticalBeadRatio = (int) ((MainMenuActivity.SCREEN_HEIGHT - topShade.getHeightScaled()) / bLastSprite
				.getHeightScaled());
		this.numberOfBeads = verticalBeadRatio * 2 - 10;

		// if (numberOfBeads > 30) {
		// numberOfBeads = 30;
		// } else
		if (numberOfBeads < 7) {
			numberOfBeads = 15;
		}

		float pNewY = bLastSprite.getY();
		float pNewX = 0;
		// create Beads bodies
		int beadVer=1;
		for (int i = 0; i < numberOfBeads - 1; i++) {
			if (i < (numberOfBeads + 1) / 2) {
				pNewY = pNewY + bLastSprite.getHeight();
				pNewX = CAMERA_WIDTH * 0.25f;// -
							     // (bFirstSprite.getWidthScaled()
							     // / 2);
			} else {
				pNewY = pNewY - bLastSprite.getHeight();
				pNewX = CAMERA_WIDTH * 0.75f;// -
							     // (bFirstSprite.getWidthScaled()
							     // / 2);
				if (i != numberOfBeads) {
				}
			}

			final Sprite bNewSprite = new Sprite(pNewX, pNewY, mBeanTextureRegions.get(beadVer),
					mEngine.getVertexBufferObjectManager());
			Body bNewBody = createBeadBody(bNewSprite);

			this.mPhysicsWorld.registerPhysicsConnector(new BeadPhysicsConnector(bLastBody, bLastSprite, bNewBody,
					bNewSprite, true, true));

			beadSpritesArray.add(bNewSprite);

			bLastBody = bNewBody;
			bLastSprite = bNewSprite;

			if (beadVer ==4) {
				beadVer = 0;
			}else{
				beadVer++;
			}
		}
		// attach beads to the scene
		for (int i = 0; i < beadSpritesArray.size(); i++) {
			this.coverScene.attachChild(beadSpritesArray.get(i));
			this.coverScene.registerTouchArea(beadSpritesArray.get(i));

		}

	}

	public void attachBeadChainByPulleyJoint() {
		Sprite mFirstBeadSprite = this.beadSpritesArray.get(0);
		Sprite mLastBeadSprite = this.beadSpritesArray.get(beadSpritesArray.size() - 1);
		Body mFirstBead = mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(mFirstBeadSprite);
		Body mLastBead = mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(mLastBeadSprite);

		this.topPulleyJoint = new PulleyJointDef();
		topPulleyJoint.initialize(
				mFirstBead,
				mLastBead,
				new Vector2((CAMERA_WIDTH * 0.25f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (topShade
						.getHeightScaled() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT)),
				new Vector2((CAMERA_WIDTH * 0.75f) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (topShade
						.getHeightScaled() / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT)), mFirstBead
						.getWorldCenter(), mLastBead.getWorldCenter(), 1f);
		topPulleyJoint.maxLengthA = mFirstBeadSprite.getHeight() * 2 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		topPulleyJoint.maxLengthB = mLastBeadSprite.getHeight() * 2 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		mPhysicsWorld.createJoint(topPulleyJoint);
	}

	private Body createBeadBody(Sprite beadSpriteX) {
		// final PhysicsEditorLoader mPhysicsLoader = new
		// PhysicsEditorLoader();
		// try {
		// mPhysicsLoader.load(this, this.mPhysicsWorld,
		// "shapes/Bead.xml", beadSpriteX, false, false);
		// System.out.println("num of bodies >>" +
		// mPhysicsLoader.getBodies().size());
		// } catch (IOException e) {
		// System.err.println("OPS !! Error at 234 \n\n\n");
		// e.printStackTrace();
		// }
		// Body body = mPhysicsLoader.getBodies().get(0);
		//
		// mPhysicsLoader.reset();
		//
		// return body;

		return PhysicsFactory.createBoxBody(this.mPhysicsWorld, beadSpriteX, BodyType.DynamicBody,
				PhysicsFactory.createFixtureDef(99, 0f, 0f));
	}

	public void setCoverRoofPosition(float pY) {
		if (this.mRoof != null) {
			if (pY > CAMERA_HEIGHT) {
				pY = CAMERA_HEIGHT;
			} else if (pY < this.topShade.getHeightScaled()) {
				pY = this.topShade.getHeightScaled();
			}
			Body mRoofBody = mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(this.mRoof);
			mRoofBody.setTransform(new Vector2((this.mRoof.getWidthScaled() / 2)
					/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
					(pY / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT)), 0);

		} else {
			System.err.println("unable to change the the Cover Scene position: mRoof is null");
		}
		return;
	}

	public float getCoverRoofPosition() {
		// Body mRoofBody =
		// mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(this.mRoof);
		// return
		// mRoofBody.getPosition().y*PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

		return this.mRoof.getY();
	}

	public float getCoverTopPosition() {
		return this.coverScene.getY();
	}

	public void spinToRight() {
		Body first = mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(beadSpritesArray.get(0));
		topPulleyJoint.bodyA = mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(beadSpritesArray.get(1));
		first.setTransform(
				new Vector2(CAMERA_WIDTH * 0.75f, topShade.getY() + beadSpritesArray.get(0).getHeightScaled()), 0);

	}

	public void scaleToFillWidth(Sprite shape) {
		shape.setScaleCenter(0, 0);
		shape.setScale(this.CAMERA_WIDTH / shape.getWidth());
	}

	public boolean isCoverUp() {
		if (getCoverTopPosition() == 0) {
			return true;
		}
		return false;
	}

	public boolean isCoverDown() {
		if (getCoverRoofPosition() == CAMERA_HEIGHT) {
			return true;
		}
		return false;
	}

	// public void openCover() {
	// System.out.println("opening Cover");
	// Body mBody =
	// this.mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(mRoof);
	// mBody.setLinearVelocity(0, -getCoverScenePosition());
	//
	// }

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	class CoverSceneTouchHandler implements IOnSceneTouchListener {
		float distanceMoved = 0;
		float origin = 0;
		float originScenePos;

		@Override
		public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
			if (// pSceneTouchEvent.getY() < getCoverTopPosition()
			    // ||
			pSceneTouchEvent.getY() > getCoverRoofPosition() && !isCoverTouched || isMainTouched) {
				return false;
			}

			if (pSceneTouchEvent.isActionDown()) {
				isCoverTouched = true;
				origin = pSceneTouchEvent.getY();
				originScenePos = getCoverRoofPosition();
			} else if (pSceneTouchEvent.isActionMove()) {

				distanceMoved = (pSceneTouchEvent.getY() - origin);

				setCoverRoofPosition(originScenePos + distanceMoved);

			} else if (pSceneTouchEvent.isActionUp()) {
				isCoverTouched = false;
				origin = 0;
			}

			return true;
		}

	}

	class BeadSprite extends Sprite {

		public BeadSprite(float pX, float pY, float pWidth, float pHeight, ITextureRegion pTextureRegion,
				ISpriteVertexBufferObject pSpriteVertexBufferObject) {
			super(pX, pY, pWidth, pHeight, pTextureRegion, pSpriteVertexBufferObject);
		}

		@Override
		public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			System.out.println("BEAD AREA TOUCHED");
			return true;
		}

	}

	class BeadPhysicsConnector extends PhysicsConnector {

		Sprite newSprite;
		Sprite prevSprite;
		Body newBody;
		Body prevBody;
		Line connectionLine;
		private DistanceJointDef distanceJoint;

		public BeadPhysicsConnector(Body newBeadBody, Sprite newBeadSprite) {
			super(newBeadSprite, newBeadBody);

		}

		public BeadPhysicsConnector(Body prevBeadBody, Sprite prevBeadSprite, Body newBeadBody, Sprite newBeadSprite,
				boolean pUdatePosition, boolean pUpdateRotation) {
			super(newBeadSprite, newBeadBody, pUdatePosition, pUpdateRotation);

			this.prevSprite = prevBeadSprite;
			this.newSprite = newBeadSprite;
			this.prevBody = prevBeadBody;
			this.newBody = newBeadBody;

			connectionLine = new Line(0, 0, 0, 0, getVertexBufferObjectManager());
			connectionLine.setLineWidth(1f);
			coverScene.attachChild(connectionLine);
			createBeadJoint();
		}

		private void createBeadJoint() {
			Vector2 pAnchorA, pAnchorB;

			if (beadSpritesArray.size() < numberOfBeads / 2) {
				pAnchorA = new Vector2(prevBody.getWorldCenter().x, prevBody.getWorldCenter().y
						+ (prevSprite.getHeight() / (PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT * 2)));
				pAnchorB = new Vector2(newBody.getWorldCenter().x, newBody.getWorldCenter().y
						- (newSprite.getHeight() / (PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT * 2)));
			} else if (beadSpritesArray.size() == numberOfBeads / 2) {
				pAnchorA = new Vector2(prevBody.getWorldCenter().x, prevBody.getWorldCenter().y
						+ (prevSprite.getHeight() / (PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT * 2)));
				pAnchorB = new Vector2(newBody.getWorldCenter().x, newBody.getWorldCenter().y
						+ (newSprite.getHeight() / (PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT * 2)));
			} else {
				pAnchorA = new Vector2(prevBody.getWorldCenter().x, prevBody.getWorldCenter().y
						- (prevSprite.getHeight() / (PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT * 2)));
				pAnchorB = new Vector2(newBody.getWorldCenter().x, newBody.getWorldCenter().y
						+ (newSprite.getHeight() / (PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT * 2)));
			}

			distanceJoint = new DistanceJointDef();
			distanceJoint.initialize(prevBody, newBody, pAnchorA, pAnchorB);
			distanceJoint.collideConnected = true;
			distanceJoint.length = 0.5f;
			// connectionLineJoint.frequencyHz = 100f;
			// connectionLineJoint.dampingRatio = 0.5f;
			mPhysicsWorld.createJoint(distanceJoint);

		}

		@Override
		public void onUpdate(float pSecondsElapsed) {
			super.onUpdate(pSecondsElapsed);
			// coordinates are in meters to be used in the scene

			// if (this.mShape.getY() < topShade.getY() +
			// topShade.getHeightScaled()) {
			// this.mBody.setLinearVelocity(0, 50);
			// } else {
			// this.mBody.setLinearVelocity(0, 0);
			// }

			if (connectionLine != null) {
				Vector2 aVector = prevBody.getWorldPoint(new Vector2(0f, 0f));
				Vector2 bVector = newBody.getWorldPoint(new Vector2(0f, 0f));

				connectionLine.setPosition(aVector.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, aVector.y
						* PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, bVector.x
						* PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, bVector.y
						* PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
			}

			if (this.mBody.equals(mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(beadSpritesArray.get(0)))
			// ||(beadSpritesArray.size() ==
			// numberOfBeads&&this.mBody.equals(mPhysicsWorld.getPhysicsConnectorManager().findBodyByShape(
			// beadSpritesArray.get(numberOfBeads-1))))
			) {
				// TODO add lines
				// this.mBody.applyLinearImpulse(new
				// Vector2(0,2000),
				// this.mBody.getWorldCenter());
			}

		}

	}
}
