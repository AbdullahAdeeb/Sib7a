package com.baramej.sib7a;

import java.io.IOException;
import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
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
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andlabs.andengine.extension.physicsloader.PhysicsEditorLoader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga
 * 
 * @author Nicolas Gramlich
 * @since 18:47:08 - 19.03.2010
 */
public class Sib7aPhysicsWorld extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final int CAMERA_HEIGHT = 720;
	protected static final int CAMERA_WIDTH = 480;
	protected static final int ENGINE_REFRESH_RATE = 50;
	protected final String BEAD_PNG_NAME = "Bead1.png";

	// ===========================================================
	// Fields
	// ===========================================================

	private Vibrator vibrator;
	public static SharedPreferences usrPref = null;
	static int sib7aIndex = 0;
	Intent settingsDialongIntent;
	long[] LONG_VIBE_PATTERN = { 0, 200, 50, 200 };
	long[] SHORT_VIBE_PATTERN = { 0, 50, 50, 50 };
	SoundPool sPool = null;
	int[] soundsIds = new int[3];

	private Scene mScene;
	private BuildableBitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mBackgroundRegion;
	private ITextureRegion mBeanTextureRegion;
	// private ITiledTextureRegion mCircleFaceRegion;
	private FixtureDef beadFixtureDef;

	protected PhysicsWorld mPhysicsWorld;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), camera);

	}

	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new Engine(pEngineOptions);
		// return new FixedStepEngine(pEngineOptions,
		// ENGINE_REFRESH_RATE);
	}

	@Override
	public void onCreateResources() {
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		usrPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		loadSounds();

		beadFixtureDef = PhysicsFactory.createFixtureDef(1f, 0f, 0f);

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(mEngine.getTextureManager(), 1000, 1000,
				BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
		this.mBeanTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this,
				BEAD_PNG_NAME);
		this.mBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this,
				"black_abstract_background.png");
		// this.mCircleFaceRegion =
		// BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas,
		// this, "Bead1.png", 1, 1);
		try {
			this.mBitmapTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0,
							0, 0));
		} catch (TextureAtlasBuilderException e) {
			System.out.println("OPS! something went wrong at 451");
			e.printStackTrace();
		}

		this.mBitmapTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		// this.mScene.setBackground(new Background(0.5f, 0.5f, 0.5f));
		this.mScene.setBackground(new SpriteBackground(new Sprite(0, 0, this.mBackgroundRegion, mEngine
				.getVertexBufferObjectManager())));
		this.mScene.setOnSceneTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH * 4), false);

		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();

//		 final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT,
//		 CAMERA_WIDTH, 0, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 0, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 0, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH, 0, 0, CAMERA_HEIGHT, vertexBufferObjectManager);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.2f, 0.5f);
//		 PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground,
//		 BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

//		 this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);

		initBeads();

		return this.mScene;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if (this.mPhysicsWorld != null) {
			if (pSceneTouchEvent.isActionDown()) {
				// TODO IMPLEMENT TOUCH COUNT
				// this.addFace(pSceneTouchEvent.getX(),
				// pSceneTouchEvent.getY());
				return true;
			}
		}
		return false;
	}

	@Override
	public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {

	}

	@Override
	public void onAccelerationChanged(final AccelerationData pAccelerationData) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it
		// is present.
		settingsDialongIntent = new Intent(this, SettingsDialog.class);
		getMenuInflater().inflate(R.menu.sib7a, menu);
		// this.startActivity(settingsDialongIntent);
		System.out.println("OnCreateOptionsMenu!! <<<<<<");
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 24 || keyCode == 25) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == 24 || keyCode == 25) {
			onActionClick();
			return true;
		} else if (keyCode == 82) {
			return startSettingsDialog();
		}
		return super.onKeyUp(keyCode, event);
	}

	private boolean startSettingsDialog() {
		this.startActivity(settingsDialongIntent);
		return true;
	}

	private void onActionClick() {
		// sib7a.incrementCount();
		vibrate();
		playSound(0);
	}

	protected static int getMaxCount() {
		if (sib7aIndex == 0) {
			return 33;
		} else if (sib7aIndex == 1) {
			return 100;
		} else if (sib7aIndex == 2) {
			int x = Integer.parseInt(usrPref.getString("userDefinedCount", "0"));
			return x;
		}
		return 0;
	}

	private void playSound(int index) {
		if (!usrPref.getBoolean("isSound", true)) {
			return;
		}
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		sPool.play(soundsIds[index], volume, volume, 2, 0, 1);
	}

	/*
	 * Vibrationh
	 */
	private void vibrate() {
		int dummy = 1;
		if (!usrPref.getBoolean("isVibrate", true)) {
			return;
		}
		if (dummy == getMaxCount() / 3 || dummy == (getMaxCount() / 3) * 2) {
			shortVib();
		} else if (dummy == getMaxCount()) {
			longVib();
		} else {
			clickVib();
		}
	}

	// TODO implement this as ENUM
	private void longVib() {
		vibrator.vibrate(LONG_VIBE_PATTERN, -1);
	}

	private void clickVib() {
		vibrator.vibrate(25);
	}

	private void shortVib() {
		vibrator.vibrate(SHORT_VIBE_PATTERN, -1);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void initBeads() {
		ArrayList<Sprite> beadSpritesArray = new ArrayList<Sprite>();

		final Sprite anchorSpriteA = new Sprite(CAMERA_WIDTH * 0.6f, 0, this.mBeanTextureRegion,
				this.getVertexBufferObjectManager());
//		final Sprite anchorSpriteB = new Sprite(0, CAMERA_HEIGHT * 0.85f, this.mBeanTextureRegion,
//				this.getVertexBufferObjectManager());
		final Sprite anchorSpriteB = new Sprite(0, CAMERA_HEIGHT * 0.85f, this.mBeanTextureRegion,
				this.getVertexBufferObjectManager());

		final Body anchorBodyA = PhysicsFactory.createBoxBody(this.mPhysicsWorld, anchorSpriteA, BodyType.StaticBody,
				PhysicsFactory.createFixtureDef(99, 0f, 0.2f));
		final Body anchorBodyB = PhysicsFactory.createBoxBody(this.mPhysicsWorld, anchorSpriteB, BodyType.StaticBody,
				PhysicsFactory.createFixtureDef(99, 0.1f, 0.0f));

		anchorBodyB.setTransform(anchorBodyB.getWorldCenter(), (float) (-Math.PI / 2));
		beadSpritesArray.add(anchorSpriteA);
		beadSpritesArray.add(anchorSpriteB);

		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(anchorSpriteA, anchorBodyA));
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(anchorSpriteB, anchorBodyB));

		Sprite originSprite = anchorSpriteA;
		Body originBody = anchorBodyA;

		final PhysicsEditorLoader mPhysicsLoader = new PhysicsEditorLoader();

		// create Beads bodies
		for (int i = 0; i < 9; i++) {
			float pPlacementY = originSprite.getY() + originSprite.getHeight();
			final Sprite beadSpriteX = new Sprite(CAMERA_WIDTH / 2, pPlacementY, this.mBeanTextureRegion,
					this.getVertexBufferObjectManager());
			try {
				mPhysicsLoader.load(this, this.mPhysicsWorld, "shapes/Bead.xml", beadSpriteX, false, false);
				System.out.println("num of bodies >>" + mPhysicsLoader.getBodies().size());
			} catch (IOException e) {
				System.err.println("OPS !! Error at 234 \n\n\n");
				e.printStackTrace();
			}
			Body beadBodyX = mPhysicsLoader.getBodies().get(0);
			mPhysicsLoader.reset();

			this.mPhysicsWorld.registerPhysicsConnector(new BeadPhysicsConnector(originBody, originSprite, beadBodyX,
					beadSpriteX, true, true));

			beadSpritesArray.add(beadSpriteX);

			originBody = beadBodyX;
			originSprite = beadSpriteX;
		}

		// attach beads to the scene
		for (int i = 0; i < beadSpritesArray.size(); i++) {
			this.mScene.attachChild(beadSpritesArray.get(i));
		}

		Vector2 pAnchorA = new Vector2(originBody.getWorldCenter().x, originBody.getWorldCenter().y
				+ (originSprite.getHeight() / (PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT * 2)));
		Vector2 pAnchorB = new Vector2(anchorBodyB.getWorldCenter().x
				+ (anchorSpriteB.getWidth() / (PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT * 2)),
				anchorBodyB.getWorldCenter().y);

		final DistanceJointDef connectionLineLast = new DistanceJointDef();
		connectionLineLast.initialize(originBody, anchorBodyB, pAnchorA, pAnchorB);
		connectionLineLast.collideConnected = true;
		connectionLineLast.length = 0.5f;
		 this.mPhysicsWorld.createJoint(connectionLineLast);

	}

	private void loadSounds() {
		sPool = new SoundPool(2, AudioManager.FX_KEY_CLICK, 0);
		// load the sound that will be played at the beginning
		// first, to give
		// time to load
		try {
			soundsIds[2] = sPool.load(getAssets().openFd("soundFx/marbles_long.mp3"), 1);
		} catch (IOException e) {
			System.err.println("unable to load sound");
		}
		try {
			soundsIds[1] = sPool.load(getAssets().openFd("soundFx/marbles_short.mp3"), 1);
		} catch (IOException e) {
			System.err.println("unable to load sound");
		}
		try {
			soundsIds[0] = sPool.load(getAssets().openFd("soundFx/single_marble.mp3"), 2);
		} catch (IOException e) {
			System.err.println("unable to load sound");
		}

	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	class BeadPhysicsConnector extends PhysicsConnector {

		Sprite newSprite;
		Sprite prevSprite;
		Body newBody;
		Body prevBody;
		final Line connectionLine;
		private DistanceJointDef connectionLineJointX;

		public BeadPhysicsConnector(Body prevBeadBody, Sprite prevBeadSprite, Body newBeadBody, Sprite newBeadSprite,
				boolean pUdatePosition, boolean pUpdateRotation) {
			super(newBeadSprite, newBeadBody, pUdatePosition, pUpdateRotation);

			this.prevSprite = prevBeadSprite;
			this.newSprite = newBeadSprite;
			this.newBody = newBeadBody;
			this.prevBody = prevBeadBody;
			connectionLine = new Line(0, 0, 0, 0, getVertexBufferObjectManager());
			connectionLine.setLineWidth(0.2f);
			mScene.attachChild(connectionLine);
			createBeadJoint();
		}

		private void createBeadJoint() {
			Vector2 pAnchorA = new Vector2(prevBody.getWorldCenter().x, prevBody.getWorldCenter().y
					+ (prevSprite.getHeight() / (PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT * 2)));
			Vector2 pAnchorB = new Vector2(newBody.getWorldCenter().x, newBody.getWorldCenter().y
					- (newSprite.getHeight() / (PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT * 2)));

			connectionLineJointX = new DistanceJointDef();
			connectionLineJointX.initialize(prevBody, newBody, pAnchorA, pAnchorB);
			connectionLineJointX.collideConnected = true;
			connectionLineJointX.length = 0.5f;

			mPhysicsWorld.createJoint(connectionLineJointX);

		}

		@Override
		public void onUpdate(float pSecondsElapsed) {
			super.onUpdate(pSecondsElapsed);
			// coordinates are in meters to be used in the scene

			// float x1 = (this.connectionLineJointX.localAnchorA.x
			// +prevBody.getWorldCenter().x)*
			// PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			// float y1 =
			// (this.connectionLineJointX.localAnchorA.y+prevBody.getWorldCenter().y)*
			// PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			// float x2 = (this.connectionLineJointX.localAnchorB.x
			// +newBody.getWorldCenter().x)*
			// PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			// float y2 = (this.connectionLineJointX.localAnchorB.y
			// +newBody.getWorldCenter().y)*
			// PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			//
			// connectionLine.setPosition(x1, y1, x2, y2);
			connectionLine.setPosition(prevBody.getWorldCenter().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
					prevBody.getWorldCenter().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
					newBody.getWorldCenter().x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
					newBody.getWorldCenter().y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);

		}
	}
}
