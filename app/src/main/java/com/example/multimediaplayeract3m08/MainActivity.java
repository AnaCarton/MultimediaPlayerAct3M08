package com.example.multimediaplayeract3m08;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    //Se definen las variables que necesitamos
    //Un spinner para seleccionar el archivo que se quiere reproducir
    Spinner sourcesSpinner;
    //Tres objetos de la clase MediaPlayer para cada uno de los archivos
    MediaPlayer mediaPlayer, mp, mediaPlayerVideo;
    //Los botones de reproducción play, pause, stop, avanzar y retroceder
    Button playButton, pauseButton, stopButton, rewindButton, forwardButton;
    //TextView para mostrar información sobre los metadatos, el estado de reproducción (play, stopped, paused) y la duración de los archivos en segundos
    TextView metadataTV, durationTV, estadoTV;
    //Un VideoView para la reproducción del archivo mp4
    VideoView videoView;
    //Un MediaController para el VideoView
    MediaController mediaController;
    //Una variable de la clase SeekBar para la barra de progreso
    SeekBar seekBar;
    //Variables de tipo String para guardar los valores de autor, título de la canción y año
    String autor, titulo, annio;
    //Objetos de la clase propia MediaObserver que, en un principio, se igualan a null
    private MediaObserver observer = null;
    private MediaObserver observer2 = null;
    private MediaObserver observer3 = null;
    int length = 0;

    //Método onCreate de la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Se inicializan las variables asociándolas a elementos del layout
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        rewindButton = findViewById(R.id.rewindButton);
        forwardButton = findViewById(R.id.forwardButton);
        metadataTV = findViewById(R.id.textView_metadata);
        durationTV = findViewById(R.id.textView_duration);
        estadoTV = findViewById(R.id.textView_estado);
        videoView = findViewById(R.id.videoView2);
        sourcesSpinner = findViewById(R.id.spinnerSources);
        seekBar = findViewById(R.id.seekbar);

        //Definimos el array con las opciones que va a mostrar el spinner (en este caso son los títulos de las diferentes canciones de los archivos que se van a reproducir)
        String[] options = {"Renaissance.mp3 con create", "Renaissance.mp3 con setDataSource", "No big deal.mp4"};

        //Se declara el adaptador para el spinner y se le setea
        ArrayAdapter<String> optionsAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, options);
        sourcesSpinner.setAdapter(optionsAdapter);

        //Se crea una nueva variable de tipo MediaMetadataRetriever, que va a servir para obtener los metadatos
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();

        //Se establece un listener para los cambios en la selección de las opciones del spinner
        // ya que en ello se va a basar la reproducción de los diferentes archivos
        sourcesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //OPCIÓN 1 DEL SPINNER
                //Si se selecciona la opción 1 del spinner...
                if (sourcesSpinner.getSelectedItemPosition() == 0) {
                    //Si el objeto de MediaPlayer asociada al segundo archivo no es null, se hace release para que no se siga reproduciendo
                    if(mp!=null){
                        length = 0;
                        mp.release();
                        mp = null;
                        //Como se ha podido reproducir otro archivo anteriormente, se ha de igualar a null el objeto de MediaObserver de las otras reproducciones
                        if (observer2 != null) {
                            observer2.stop();
                        }
                    }
                    //En caso de que el videoView no sea null, se para su reproducción y se hace invisible, ya que para reproducir el mp3 no
                    //hace falta
                    if (videoView != null){
                        videoView.seekTo(0);
                        videoView.stopPlayback();
                    }
                    videoView.setVisibility(View.GONE);

                    //Se establece la fuente de datos del objeto de MetadataRetriever, pasándole como parámetros el contexto de la
                    //actividad principal y la ruta al archivo 1
                    metadataRetriever.setDataSource(MainActivity.this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.renaissance));
                    //Una vez establecido el archivo, se extraen los metadatos referentes a autor, título y año
                    autor = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
                    titulo = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    annio = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);


                    //Se establece que la barra de progreso sea visible
                    seekBar.setVisibility(View.VISIBLE);

                    //Se ponen en blanco los textViews para los metadatos, el estado y la duración del archivo
                    metadataTV.setText("Metadatos: ");
                    estadoTV.setText("Estado: ");
                    durationTV.setText("Duración: ");



                    //Se crea un listener para cuando se haga clic sobre el botón de play
                    playButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Se comprueba si mediaPlayer es null
                            if (mediaPlayer == null) {
                                //si es null, se crea y se asocia al arcivo mp3 correspondiente
                                mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.renaissance);


                                //comienza la reproducción de la canción
                                mediaPlayer.start();

                                //se obtiene la duración de la canción
                                int duration = mediaPlayer.getDuration();
                                //Se calculan los minutos
                                int minutes = (duration / 1000) / 60;
                                //Y los segundos
                                int secs = ((duration / 1000) % 60);

                                //También se calcula el total de segundos de la canción
                                int seconds = duration / 1000;

                                //durationTV.setText("Duración: " + minutes + ":" + seconds);
                                //Se establecen los textos de los textview con la información de duración, metadatos y estado de la canción (playing)
                                durationTV.setText("Duración: " + seconds + "s");
                                metadataTV.setText("Metadatos: \n Autor - " + autor + "; \n Título - " + titulo + "; \n Año - " + annio);
                                estadoTV.setText("Estado: playing");

                                //Se inicializa la variable de tipo MediaObserver para poder mantener actualizada la barra de progreso mientras
                                //se reproduce la canción; se inicia la ejecución del hilo
                                observer = new MediaObserver();
                                new Thread(observer).start();

                                /*
                                //cuando se completa la reproducción de la canción, se resetea el mediaPlayer
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        System.out.println("HOLA, ME HE COMPLETADO");
                                        observer.stop();
                                        mediaPlayer.stop();
                                        mediaPlayer.release();
                                    }
                                });*/

                            }
                            else {
                                //Si mediaPlayer no era null, se reproduce la música con start
                                System.out.println("mediaplayer no es null -> " +mediaPlayer);
                                mediaPlayer.start();
                                estadoTV.setText("Estado: playing");
                                //se inicializa el observer, para que funcione la barra de progreso
                                observer = new MediaObserver();
                                new Thread(observer).start();
                            }

                        }
                    });

                    //listener para cuando se hace clic sobre el botón de pause cuando está seleccionada la primera opción del spinner
                    pauseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Si mediaPlayer no es null y estaba reproduciéndose, se para
                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                //se para también la actualización de la barra de progreso
                                observer.stop();
                                //se establece el estado de reproducción a paused en el textview correspondiente
                                estadoTV.setText("Estado: paused");
                            }
                            //Si no se estaba reproduciendo la canción no hace nada
                        }
                    });

                    //Listener para cuando se hace clic sobre el botón de stop cuando está seleccionada la opción 1 en el spinner
                    stopButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Si mediaPlayer no es null
                            if (mediaPlayer != null) {
                                //se hace release
                                mediaPlayer.release();
                                //se para el observer
                                observer.stop();
                                // y se hace null el mediaPlayer
                                mediaPlayer = null;
                                //se establece el estado de reproducción a stopped
                                estadoTV.setText("Estado: stopped");

                            } else {
                                //Si mediaPlayer es null no se hace nada
                            }
                        }
                    });
                    //listener para cuando se hace clic sobre el botón de avanzar cuando está seleccionado la opción 1 del spinner
                    forwardButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //si mediaPlayer no es null
                            if (mediaPlayer != null) {
                                //se obtiene la posición actual de reproducción del mediaplayer...
                                int position = mediaPlayer.getCurrentPosition();
                                //se le suman 10 segundos y se aumenta la posición del mediaPlayer a ese momento
                                mediaPlayer.seekTo(position + 10000);
                            }
                        }
                    });

                    //listener para cuando se hace clic sobre el botón de retroceder cuando está seleccionado la opción 1 del spinner
                    rewindButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //si mediaPlayer no es null
                            if (mediaPlayer != null) {
                                //se obtiene la posición actual de reproducción del mediaplayer...
                                int position = mediaPlayer.getCurrentPosition();
                                //se le restan 10 segundos y se aumenta la posición del mediaPlayer a ese momento
                                mediaPlayer.seekTo(position - 10000);
                            }
                        }
                    });
                }

                //OPCIÓN 2 DEL SPINNER
                //Si se selecciona la opción 2 del spinner...


                if (sourcesSpinner.getSelectedItemPosition() == 1) {
                    //Si mediaPlayer (MediaPlayer asociado a la primera opción del spinner) no es null, se hace release
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                    mediaPlayer = null;
                        //si el observer (asociado a la primera opción del spinner) no es null, se para con stop()
                        if (observer != null) {
                            observer.stop();
                        }
                    }

                    //se establece la visibilidad del SeekBar a visible
                    seekBar.setVisibility(View.VISIBLE);

                    //si VideoView no es null, se para su reproducción (por si acaso estuviese reproduciéndose) y se establece su visibilidad a gone
                    if (videoView != null) videoView.seekTo(0);
                    videoView.stopPlayback();
                    videoView.setVisibility(View.GONE);

                    //Se establece la fuente de datos del objeto de MetadataRetriever, pasándole como parámetros el contexto de la
                    //actividad principal y la ruta al archivo 2
                    metadataRetriever.setDataSource(MainActivity.this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.renaissance));
                    //Una vez establecido el archivo, se extraen los metadatos referentes a autor, título y año
                    autor = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
                    titulo = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    annio = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);

                    //Se establecen los valores por defecto (vacíos) en los textview de duración, metadatos y estado de la canción de la opción 2
                    durationTV.setText("Duración: ");
                    metadataTV.setText("Metadatos: ");
                    estadoTV.setText("Estado: ");

                    //Se establece un listener para cuando se haga clic sobre el botón de play estando seleccionada la opción 2 en el spinner
                    playButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Se comprueba si mediaPlayer es null
                            if (mp == null) {

                                Uri myUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.renaissance);

                                mp = new MediaPlayer();
                                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                try {
                                    mp.setDataSource(getApplicationContext(), myUri);
                                    mp.prepare();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //se obtiene la duración de la canción
                                int duration = mp.getDuration();
                                //Se obtienen los minutos
                                int minutes = (duration / 1000) / 60;
                                //y los segundos
                                int secs = ((duration / 1000) % 60);

                                //se obtienen los segundos totales de duración
                                int seconds = duration / 1000;

                                //durationTV.setText("Duración: " + minutes + ":" + seconds);

                                //Se establecen los textos de los textview relativos a la duración, los metadatos y el estado de reproducción de la canción
                                durationTV.setText("Duración: " + seconds + "s");
                                metadataTV.setText("Metadatos: \n Autor - " + autor + "; \n Título - " + titulo + "; \n Año - " + annio);
                                estadoTV.setText("Estado: playing");

                                //comienza la reproducción de la canción
                                mp.start();

                                //Se inicializa observer2 para actualizar la barra del progreso a medida que se va reproduciendo la música
                                observer2 = new MediaObserver();
                                //se asocia al observer el mediaplayer correspondiente al archivo de música
                                observer2.setSource(mp);
                                new Thread(observer2).start();

                                /*
                                //cuando se completa la canción, se resetea el mediaPlayer
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        //se para la ejecución del hilo que actualiza la barra de progreso
                                        observer2.stop();
                                        mp.stop();
                                        mp.release();
                                    }
                                });*/

                            } else {


                                if(length != 0){
                                    mp.seekTo(length);
                                }
                                //Si mediaPlayer no era null, se reproduce la música con start
                                mp.start();
                                estadoTV.setText("Estado: playing");
                                //se inicializa observer2, como en el otro caso
                                observer2 = new MediaObserver();
                                observer2.setSource(mp);
                                new Thread(observer2).start();
                            }
                        }
                    });

                    //Listener para cuando se hace clic sobre el botón de pause estando seleccionada la opción 2 del spinner
                    pauseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Si mediaPlayer no es null y estaba reproduciéndose, se para
                            if (mp != null && mp.isPlaying()) {
                                mp.pause();
                                length = mp.getCurrentPosition();
                                //se para la ejecución del hilo de observer2
                                observer2.stop();
                                //se establece el estado de reproducción "paused"
                                estadoTV.setText("Estado: paused");
                            }
                            //Si no se estaba reproduciendo la canción no hace nada
                        }
                    });
                    //Listener para cuando se hace clic sobre el botón de stop estando seleccionada la opción 2 del spinner
                    stopButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Si mediaPlayer no es null
                            if (mp != null) {
                                length = 0;
                                //se hace release
                                mp.release();
                                //se para el observer asociado a la reproducción del archivo 2
                                observer2.stop();
                                // y se hace null el mediaPlayer
                                mp = null;
                                //Se establece el estado de reproducción a stopped en el textview
                                estadoTV.setText("Estado: stopped");

                            } else {
                                //Si mediaPlayer es null no se hace nada.
                            }
                        }
                    });
                    //listener para cuando se hace clic sobre el botón de avanzar cuando está seleccionado la opción 2 del spinner
                    forwardButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //si el objeto mp de tipo MediaPlayer no es null
                            if (mp != null) {
                                //se obtiene la posición actual de reproducción del mediaplayer...
                                int position = mp.getCurrentPosition();
                                //se le suman 10 segundos y se aumenta la posición del mediaPlayer a ese momento
                                mp.seekTo(position + 10000);
                            }
                        }
                    });

                    //listener para cuando se hace clic sobre el botón de retroceder cuando está seleccionado la opción 2 del spinner
                    rewindButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //si el objeto mp de tipo MediaPlayer no es null
                            if (mp != null) {
                                //se obtiene la posición actual de reproducción del mediaplayer...
                                int position = mp.getCurrentPosition();
                                //se le restan 10 segundos y se aumenta la posición del mediaPlayer a ese momento
                                mp.seekTo(position - 10000);
                            }
                        }
                    });
                }

                //OPCIÓN 3 DEL SPINNER
                //Si seleccionan la opción 3, es decir, si se selecciona el vídeo...
                if (sourcesSpinner.getSelectedItemPosition() == 2) {
                    length = 0;

                    //se comprueba si la variable de tipo MediaPlayer mp es nula o no
                    if (mp != null) {

                        //si no es null, se hace un release y se iguala a null
                        mp.release();
                        mp = null;
                        //además, si el objeto de tipo MediaObserver no es null, tenemos que pararlo
                        if (observer2 != null) {
                            observer2.stop();
                        }
                    }
                    //se comprueba si la variable de tipo MediaPlayer mediaPlayer es nula o no
                    if (mediaPlayer != null) {
                        //si no es null, se hace un release y se iguala a null
                        mediaPlayer.release();
                        mediaPlayer = null;
                        //además, si el objeto de tipo MediaObserver no es null, tenemos que pararlo
                        if (observer != null) {
                            observer.stop();
                        }

                    }
                    //Para el vídeo no necesitamos ver la barra de progreso de tipo SeekBar porque
                    //se va a utilizar la barra de progreso que proporciona la clase MediaController
                    seekBar.setVisibility(View.GONE);
                    //Se hace visible el videoView, que ahora sí es necesario
                    videoView.setVisibility(View.VISIBLE);
                    //Para recuperar los metadatos del vídeo, se ha de establecer la fuente de datos a partir del contexto de la actividad y la uri del vídeo
                    metadataRetriever.setDataSource(MainActivity.this, Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dodie));
                    //Se extraen los metadatos del vídeo referentes al autor, el título y el año de composición
                    autor = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER);
                    titulo = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    annio = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
                    //Se ponen en blanco los textView referentes a la duración, los metadatos y el estado de la canción
                    durationTV.setText("Duración: ");
                    metadataTV.setText("Metadatos: \n");
                    estadoTV.setText("Estado: ");

                    //Se establece un listener sobre el botón del play, para que actúe de la siguiente forma cuando es clicado
                    playButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //En caso de que el videoView no sea null y no se esté reproduciendo...
                            if (videoView != null && !videoView.isPlaying()) {
                                //comienza la reproducción del vídeo
                                videoView.start();
                                estadoTV.setText("Estado: playing");
                                //se inicializa un objeto de la clase MediaObserver para poder actualizar la barra de progreso
                                observer3 = new MediaObserver();
                                new Thread(observer3).start();
                            }

                            //en caso de que el vídeo no se esté reproduciendo
                            if (!videoView.isPlaying()) {
                                //Asociamos la variable con el vídeo que está en Raw
                                videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dodie));
                                //Controler del vídeo:
                                //Se inicializa la variable de tipo MediaController, pasándole como parámetro el contexto de la actividad principal
                                mediaController = new MediaController(MainActivity.this);
                                //mediaController sirve para añadir un controller al vídeo para iniciar y parar
                                mediaController.setAnchorView(videoView);
                                videoView.setMediaController(mediaController);
                                //se obtiene la duración del vídeo creando una nueva variable mediaPlayer asociada al archivo de vídeo
                                mediaPlayerVideo = MediaPlayer.create(MainActivity.this, R.raw.dodie);
                                int duration = mediaPlayerVideo.getDuration();
                                //Si quisiéramos obtener los minutos de duración
                                int minutes = (duration / 1000) / 60;
                                //Para obtener los segundos y añadirlos a los minutos...
                                int secs = ((duration / 1000) % 60);

                                //Para obtener el total de segundos de duración de la canción
                                int seconds = duration / 1000;

                                //durationTV.setText("Duración: " + minutes + ":" + seconds);

                                //Se establecen los datos de duración, estado y metadatos de la canción en los respectivos textView
                                durationTV.setText("Duración: " + seconds + "s");
                                metadataTV.setText("Metadatos: \n Autor - " + autor + "; \n Título - " + titulo + "; \n Año - " + annio);
                                estadoTV.setText("Estado: playing");

                                //comienza la reproducción del vídeo
                                videoView.start();
                                //Se inicializa una variable de tipo MediaObserver
                                observer3 = new MediaObserver();
                                observer3.setSource(mediaPlayerVideo);
                                new Thread(observer3).start();

                                /*
                                //Cuando termina la reproducción del vídeo...
                                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        observer3.stop();
                                        videoView.stopPlayback();
                                        //Se iguala a null el videoView
                                        videoView = null;
                                    }
                                });*/
                            }
                        }
                    });

                    //Listener para cuando se hace clic sobre el botón de pausa y está el vídeo seleccionado
                    pauseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Si el vídeo está reproduciéndose...
                            if (videoView.isPlaying()) {
                                //Se pausa el vídeo
                                videoView.pause();
                                //Se pausa el observer
                                observer3.stop();
                                //Se cambia el estado a paused
                                estadoTV.setText("Estado: paused");
                            }
                        }
                    });
                    //Listener para cuando se hace clic sobre el botón de stop y está el vídeo seleccionado
                    stopButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Si videoView es distinto de null
                            if (videoView != null) {
                                //Se para (stop) la reproducción del vídeo
                                videoView.stopPlayback();
                                //Se para el observer
                                observer3.stop();
                                //Se establece el estado de reproducción a stopped
                                estadoTV.setText("Estado: stopped");
                            }
                        }
                    });
                    //Se establece un listener para el botón de avanzar 10 segundos cuando está seleccionado el vídeo
                    forwardButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Si el videoView es distinto de null
                            if (videoView != null) {
                                //Se obtiene la posición actual de reproducción del vídeo
                                int position = videoView.getCurrentPosition();
                                //Se añaden 10 segundos a la posición actual y se mueve la barra de progreso hasta ese momento
                                videoView.seekTo(position + 10000);
                            }
                        }
                    });

                    //Se establece un listener para el botón de retroceder 10 segundos cuando está seleccionado el vídeo
                    rewindButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Si videoView es distinto de null
                            if (videoView != null) {
                                //Se obtiene la posición actual de reproducción del vídeo
                                int position = videoView.getCurrentPosition();
                                //Se restan 10 segundos a la posición actual y se mueve la barra de progreso hasta ese momento
                                videoView.seekTo(position - 10000);
                            }
                        }
                    });
                }
            }

            //En caso de que no haya nada seleccionado en el spinner... es un método que viene de la propia clase madre pero no es necesario en este caso porque siempre hay algo seleccionado
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    //Clase propia MediaObserver que implementa Runnable para poder establecer una barra de progreso que se vaya actualizando
    //a medida que se reproducen las canciones
    private class MediaObserver implements Runnable {
        private AtomicBoolean stop = new AtomicBoolean(false);

        //Como hay distintos objetos de tipo MediaPlayer instanciados, se crea un método al que le llega el mediaplayer en cuestión
        public void setSource(MediaPlayer mediaPlayerNow) {
            mediaPlayer = mediaPlayerNow;
        }

        //Método stop que hace que se pare de ejecutar el hilo
        public void stop() {
            stop.set(true);
        }

        //Método por defecto de Runnable que se ejecuta cuando se inicia el hilo
        @Override
        public void run() {
            //Mientras al hilo no se le mande parar con stop...
            while (!stop.get()) {
                //En un bloque try/catch se intenta establecer la posición de la barra de progreso
                //según la posición actual del mediaPlayer con respecto a la duración máxima
                try {
                    seekBar.setProgress((int) ((double) mediaPlayer.getCurrentPosition() / (double) mediaPlayer.getDuration() * 100));

                } catch (Exception e) {
                    //Si se produce una excepción, se recoge aquí para que no pare el programa
                }
                //En otro bloque try catch
                try {
                    //Se hace que el hilo duerma durante 200 milisegundos antes de volver a calcular la posición
                    //en la barra de progreso
                    Thread.sleep(200);

                } catch (Exception ex) {
                    //Si se produce una excepción se imprime el stackTrace
                    ex.printStackTrace();
                }
            }
        }
    }

    //onDestroy es uno de los métodos de la clase madre AppCompatActivity
    @Override
    protected void onDestroy() {
        //Cuando se cierra la aplicación, se llama al onDestroy de la clase madre
        super.onDestroy();
        //Se paran las reproducciones de los mediaPlayers y el videoView para que no permanezcan
        //reproduciéndose en el background
        mediaPlayer.stop();
        mp.stop();
        videoView.stopPlayback();
    }

}


