package com.example.ejemplorun

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room.databaseBuilder
import com.example.ejemplorun.DAL.TaskDatabase
import com.example.ejemplorun.DAL.TaskEntity
import com.example.ejemplorun.DAL.TasksDao
import com.example.ejemplorun.ui.theme.EjemploRunTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    /**
     * COMPANION OBJECT database de tipo TaskDatabase que sirve para poder usarse en
     * todo el codigo
     */
    companion object {
        lateinit var database: TaskDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //CINICIALIZACION BD DECLARADA EN companion object
        database = databaseBuilder(this, TaskDatabase::class.java, "piedraPapelTijeras-db").build()

        enableEdgeToEdge()
        setContent {
            //DAO
            val dao = database.tasksDao()
            val gameViewModel = GameViewModel(dao)// Inicializar ViewModel aquí
            val navController = rememberNavController()



            EjemploRunTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "GamePage",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("GamePage") {
                            GamePage(navController = navController)
                        }
                        composable("PantallaResultado/{eleccionJugador}") { backStackEntry ->
                            val eleccionJugador =
                                backStackEntry.arguments?.getString("eleccionJugador") ?: ""
                            PantallaResultado(eleccionJugador, navController, gameViewModel)
                        }

                    }
                }
            }
        }
    }
}

/**
 *Pantalla GamePage la cual se centtra en la escoger piedra papel o tijera a travez de una imagen sy navega a PantallaResultado pasando por parametro eleccionJugador
 */
@Composable
fun GamePage(navController: NavController) {
    var eleccionJugador by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(text = "Escoge una opción")
        Spacer(modifier = Modifier.height(40.dp))
        Row {
            //papel
            Image(painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.clickable {
                    eleccionJugador = "papel"
                    navController.navigate("PantallaResultado/$eleccionJugador")
                })
            Spacer(modifier = Modifier.width(40.dp))
            //piedra
            Image(painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.clickable {
                    eleccionJugador = "piedra"
                    navController.navigate("PantallaResultado/$eleccionJugador")
                })
            Spacer(modifier = Modifier.width(40.dp))
            //tijeras
            Image(painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.clickable {
                    eleccionJugador = "tijeras"
                    navController.navigate("PantallaResultado/$eleccionJugador")
                })
        }
        Spacer(modifier = Modifier.height(120.dp))
    }
}

/**
 *PantallaResultado recibe eleccionJugador y navega a PantallaResultadoPartida interactua con el  gameViewModel aumenta el num de victorias y al llegar a 5 se navega a la siguiente pagina
 */
@Composable
fun PantallaResultado(
    eleccionJugador: String,
    navController: NavController,
    gameViewModel: GameViewModel,

    ) {
    val context = LocalContext.current
    val numeroAleatorio = remember { (1..3).random() }
    var eleccionMaquina by remember { mutableStateOf("") }
    var resultadoRonda by remember { mutableStateOf("") }
    eleccionMaquina = elecionDeOpcionMaquina(numeroAleatorio)
    val imagenIdMaquina = seleccionarImagen(eleccionMaquina)
    val imagenIdJugador = seleccionarImagen(eleccionJugador)
    resultadoRonda = resultadoPartida(eleccionMaquina, eleccionJugador)


    Controlador(resultadoRonda, gameViewModel, context)

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "El jugador ha escogido: $eleccionJugador")
        Image(painter = painterResource(id = imagenIdJugador),
            contentDescription = null,
            modifier = Modifier.clickable {})

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Su oponente ha escogido: $eleccionMaquina")
        Image(painter = painterResource(id = imagenIdMaquina),
            contentDescription = null,
            modifier = Modifier.clickable {})

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Ha ganado: $resultadoRonda")
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Victorias Máquina: ${gameViewModel.victoriasMaquina}")
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Victorias Jugador: ${gameViewModel.victoriasJugador}")

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("GamePage") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Volver")
        }
    }
}

/**
 * SE ENCARGA DE CONTACTAR CON EL gameViewModel CON UN LAUCHED EFFECT PARA QUE NO SER REPITA LA LOGICA
 */
@Composable
private fun Controlador(
    resultadoRonda: String, gameViewModel: GameViewModel, context: Context
) {
    LaunchedEffect(resultadoRonda) {
        if (resultadoRonda == "Jugador") {
            gameViewModel.incrementarVictoriasJugador()
        } else if (resultadoRonda == "Maquina") {
            gameViewModel.incrementarVictoriasMaquina()
        }
        Toast.makeText(context, "Ha ganado: $resultadoRonda", Toast.LENGTH_SHORT).show()
    }
}

/**
 * DEVUELVE UN STRING RESULTADO RONDA DEPENDIENDO DE LA ELLECION DE LOS JUGADORES
 */
private fun resultadoPartida(
    eleccionMaquina: String, eleccionJugador: String
): String {
    val resultadoRonda: String = when {
        eleccionMaquina == eleccionJugador -> "empate"
        eleccionMaquina == "tijeras" && eleccionJugador == "piedra" -> "Jugador"
        eleccionMaquina == "piedra" && eleccionJugador == "tijeras" -> "Maquina"
        eleccionMaquina == "papel" && eleccionJugador == "tijeras" -> "Jugador"
        eleccionMaquina == "tijeras" && eleccionJugador == "papel" -> "Maquina"
        eleccionMaquina == "piedra" && eleccionJugador == "papel" -> "Jugador"
        eleccionMaquina == "papel" && eleccionJugador == "piedra" -> "Maquina"
        else -> "empate"
    }
    return resultadoRonda
}

/**
 * Selecciona la ruta imagen dependiendo de si la variable eleccion
 */

private fun seleccionarImagen(eleccion: String): Int {
    val imagenId = when (eleccion) {
        "piedra" -> R.drawable.ic_launcher_foreground
        "papel" -> R.drawable.ic_launcher_foreground
        "tijeras" -> R.drawable.ic_launcher_foreground
        else -> R.drawable.ic_launcher_background
    }
    return imagenId
}

/**
 * Iguala la variable eleccion maquina a un valor depediendo de un numero aleatorio
 */

private fun elecionDeOpcionMaquina(numeroAleatorio: Int): String {
    val eleccionMaquina: String = when (numeroAleatorio) {
        1 -> "piedra"
        2 -> "papel"
        else -> "tijeras"
    }
    return eleccionMaquina
}


/**
 *game view model guarda los datos de vitorias y interactua con la BD
 */
class GameViewModel(private val dao: TasksDao) : ViewModel() {
    var victoriasMaquina by mutableIntStateOf(0)
    var victoriasJugador by mutableIntStateOf(0)

    /**
     * AL LANZAR EL VIEW MODEL carga la BD COJE LOS VALORES DE  dao si dao esta vacio lo iiguala
     */
    init {
        viewModelScope.launch {
            try {
                val entidad = dao.getAll()
                if (entidad != null) {
                    victoriasJugador = entidad.victoriasJugador1
                    victoriasMaquina = entidad.victoriasMaquina
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error al cargar  la BD", e)
            }
        }
    }

    /**
     * INCREMENTA LA VARIABLE victoriasMaquina
     */
    fun incrementarVictoriasMaquina() {
        victoriasMaquina++
        actualizarEnBaseDeDatos()
    }

    /**
     * INCREMENTA LA VARIABLE victoriasJugador
     */
    fun incrementarVictoriasJugador() {
        victoriasJugador++
        actualizarEnBaseDeDatos()
    }

    /**
     *ACTUALIZA LA BD SI ES NULL HACE CREA UNA NUEVA ENTIDAD LA RELLENA CON LAS VARIABLES LOCALES Y HACE EL INSERT
     */
    private fun actualizarEnBaseDeDatos() {
        viewModelScope.launch {
            try {
                var entidad = dao.getAll()
                if (entidad == null) {
                    entidad = TaskEntity(
                        victoriasJugador1 = victoriasJugador, victoriasMaquina = victoriasMaquina
                    )
                    dao.insert(entidad)
                } else {
                    entidad.victoriasJugador1 = victoriasJugador
                    entidad.victoriasMaquina = victoriasMaquina
                    dao.update(entidad)
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error al actualizar los datos", e)
            }
        }
    }
}
