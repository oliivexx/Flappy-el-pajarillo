/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;


import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
  	final Drop game;

	Texture tuboArriba;
        Texture tuboAbajo;
	Texture imagenPajaro;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	Rectangle pajaro;
	Array<Rectangle> tubosAbajo;
        Array<Rectangle> tubosArriba;
        int contador = 1;
	long lastDropTime;
	int dropsGathered;
        float yVelocity;
        float numeroAleatorio;
        float xVelocity = 0;
        boolean ahoraCuento;
        
        final float GRAVITY = -28f;
        final float DISTANCE = 600;
        final float MAX_VELOCITY = 20f;
        final float DAMPING = 0.87f;

	public GameScreen(final Drop gam) {
		this.game = gam;

		// load the images for the droplet and the bucket, 64x64 pixels each
		tuboArriba = new Texture(Gdx.files.internal("toptube.png"));
                tuboAbajo = new Texture (Gdx.files.internal("bottomtube.png"));
		imagenPajaro = new Texture(Gdx.files.internal("bucket.png"));

		// load the drop sound effect and the rain background "music"
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		rainMusic.setLooping(true);

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		
		pajaro = new Rectangle();
		pajaro.x = 40; 
		pajaro.y = 480 / 2 - 64 / 2; 
						
		pajaro.width = 64;
		pajaro.height = 64;

		// create the raindrops array and spawn the first raindrop
		tubosAbajo = new Array<Rectangle>();
                tubosArriba = new Array<Rectangle>();
		spawnRaindrop();

	}

	private void spawnRaindrop() {
		Rectangle tuboAbajoAux = new Rectangle();
		tuboAbajoAux.x = 800; 
		tuboAbajoAux.y = MathUtils.random(-300, -150);
		tuboAbajoAux.width = 68;
		tuboAbajoAux.height = 420;
		tubosAbajo.add(tuboAbajoAux);
                
                Rectangle tuboArribaAux = new Rectangle();
		tuboArribaAux.width = 68;
		tuboArribaAux.height = 420;
                tuboArribaAux.x = 800; 
		tuboArribaAux.y = tuboAbajoAux.y + DISTANCE;
                
		tubosAbajo.add(tuboArribaAux);
		lastDropTime = TimeUtils.nanoTime();
	}
        
        public void saltoPajaro(){
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                pajaro.y += 600 *  Gdx.graphics.getDeltaTime();
                yVelocity = 300;
            }  
        }

	@Override
	public void render(float delta) {
		// clear the screen with a dark blue color. The
		// arguments to glClearColor are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor(0.5f, 0.5f, 1, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		game.batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw tubos and pajaro
		game.batch.begin();
		game.font.draw(game.batch, "Tubos pasados: " + dropsGathered, 20, 480);
		game.batch.draw(imagenPajaro, pajaro.x, pajaro.y);
                
		for (Rectangle tubo : tubosAbajo) {
                    if(contador % 2 == 0){
                        game.batch.draw(tuboArriba, tubo.x, tubo.y);  
                    } else {
                        game.batch.draw(tuboAbajo, tubo.x, tubo.y);  
                    }
                contador++;
                        
		}
		game.batch.end();
               
            yVelocity = yVelocity + GRAVITY;
            float y = pajaro.getY();

            float yChange = yVelocity * delta;
            pajaro.setPosition(40, y + yChange);
                
                

		saltoPajaro();
                
                if (Gdx.input.isKeyPressed(Keys.DOWN)){
                    pajaro.y -= 200 * Gdx.graphics.getDeltaTime();
                }


		// make sure the bucket stays within the screen bounds
		if (pajaro.y < 0)
			pajaro.y = 0;
		if (pajaro.y > 480 - 64)
			pajaro.y = 480 - 64;

		// check if we need to create a new raindrop
		if (TimeUtils.nanoTime() - lastDropTime > 1500000000)
			spawnRaindrop();

		
		Iterator<Rectangle> iter = tubosAbajo.iterator();
                //Iterator<Rectangle> iter2 = tubosArriba.iterator();
                
		while (iter.hasNext()) {
			Rectangle tuboIterador = iter.next();
                        //Rectangle tuboIterador2 = iter2.next();
                        
			tuboIterador.x -= 350 * Gdx.graphics.getDeltaTime();
                        //tuboIterador2.x -= 200 * Gdx.graphics.getDeltaTime();
                        
			if (tuboIterador.x +68 < 0){
                          if(ahoraCuento == false){
                            dropsGathered++;
                            ahoraCuento = true;

                            }else {
                                ahoraCuento = false;
                            }
         
                        }
                        
                        if(tuboIterador.x < -68){
                            iter.remove();
                        }
				
                       
			if (tuboIterador.overlaps(pajaro)) {
                            game.setScreen(new LoseScreen(game));
			}
                        
                        if(pajaro.y <= 0){
                            game.setScreen(new LoseScreen(game));
                        }
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// start the playback of the background music
		// when the screen is shown
		rainMusic.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		tuboArriba.dispose();
		imagenPajaro.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}

}
