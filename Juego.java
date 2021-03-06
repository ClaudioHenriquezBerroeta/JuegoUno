import java.util.Scanner;

class Juego{
	private Naipe mazo;
	private int cantidadJugadores;
	private Jugador [] jugadores;
	private int cantidadCartasMano= Config.cantidadCartasMano;
	private int cantidadCartasTotal=Config.cantidadCartasNaipe;
	private int cantidadCartasRestantes;
	private int cartaNumeroPozo;

	Juego(int cantidadJugadores){
		this.mazo=new Naipe();
		if(cantidadJugadores>4){
			System.out.println("El máximo es 4 jugadores!");
			this.cantidadJugadores=4;
		}else if(cantidadJugadores<=1){
			System.out.println("El mínimo es 1 jugador contra el computador!");
			this.cantidadJugadores=2;
		}else{
			this.cantidadJugadores=cantidadJugadores;
		}
		this.jugadores = new Jugador[this.cantidadJugadores];
		this.cantidadCartasRestantes=0;

		asignarJugadores();
		repartir();		
	}
	//************************************************************************//
	// Métodos privados asociados a la creación de un Juego
	// Método para asignar turnos a jugadores, se llama al crear un Juego
	private void asignarJugadores(){
		Scanner teclado = new Scanner(System.in);
		boolean [] turnos= new boolean[4];
		boolean flag;
		String nombre;
		String pais;
		int turno;

		for(int i=1;i<=this.cantidadJugadores;i++){
			flag=false;
			turno=1;
			if(i==1){
				System.out.println("Ingrese nombre");
				nombre=teclado.nextLine();
				System.out.println("Ingrese pais");
				pais=teclado.nextLine();
			}else{
				nombre="Jugador"+i;
				pais="Algun lugar del mundo";
			}
			while(!flag){
				turno = (int)(Math.random()*10);
				if(((turno<this.cantidadJugadores)&&(turno>=0))&&(!turnos[turno])){
					flag=true;
					turnos[turno]=true;
					turno=turno+1;
				}
			}
			jugadores[i-1]=new Jugador(nombre,pais,turno);
		}
	}
	// Método que genera las manos iniciales para cada jugador, 
	// se llama al crear un Juego
	private void repartir(){
		boolean flag=false;
		int cartaNumero=-1;

		for(int j=0;j<this.cantidadJugadores;j++){
			for(int i=1;i<=this.cantidadCartasMano;i++){
				flag=false;
				while(!flag){
					cartaNumero = (int)(Math.random()*1000);
					cartaNumero = cartaNumero%this.cantidadCartasTotal;
					if(((cartaNumero<Config.cantidadCartasNaipe)&&(cartaNumero>=0))&&(mazo.getJugadorAsignado(cartaNumero)==-1)){
						flag=true;
						jugadores[j].getMano().agregarCarta(mazo.getCarta(cartaNumero),cartaNumero);
						mazo.setJugadorAsignado(cartaNumero,j);
					}
				}
			}	
		}		
	}
	//************************************************************************//
	// Método que es llamado para jugar un Juego creado
	public void jugar(){
		Scanner teclado = new Scanner(System.in);
		int carta;
		boolean ganador=false;

		this.cartaNumeroPozo=generarCarta();
		// Acá se debe modificar para terminar el juego
		// Por ahora está en un ciclo infinito
		// se muestra la carta del pozo y se juega por el usuario
		// y luego juega el computador, está solo con dos jugadores
		while(!ganador){
			System.out.print("\033[H\033[2J");
        	System.out.flush();
			System.out.println("La carta del pozo es:");
			mostrarPozo(mazo.getCarta(this.cartaNumeroPozo));
			System.out.println("Presione enter para continuar");
			teclado.nextLine();

			//Jugada del jugador usuario
			jugada(0);
			System.out.println("La carta del pozo es:");
			mostrarPozo(mazo.getCarta(this.cartaNumeroPozo));
			System.out.println("Presione enter para continuar");			
			teclado.nextLine();
			
			//Jugada del jugador Computador
			jugada(1);
			//System.out.print("\033[H\033[2J");
        	//System.out.flush();
		}
	}

	// Método que selecciona una carta de forma aleatoria de un mazo
	// retorna el número de la carta entre los valores 0 y 107
	private int generarCarta(){
		int cartaNumero=-1;
		boolean flag=false;

		while(!flag){
			cartaNumero = (int)(Math.random()*1000);
			cartaNumero = cartaNumero%108;
			if(((cartaNumero<Config.cantidadCartasNaipe)&&(cartaNumero>=0))&&(mazo.getJugadorAsignado(cartaNumero)==-1)){
				flag=true;
			}
		}
		return cartaNumero;
	}

	// Método que realiza una jugada para un jugador, ya sea usuario o computador
	// recibe el número del jugador, 0 para el usuario y 1,2 o 3 para computador 
	private void jugada(int jugador){
		int cartaNumero=-1;
		Carta carta;
		boolean flag=false;
		Scanner teclado = new Scanner(System.in);
		//valida que el jugador tenga al menos una carta válida pra jugar
		if(validarMano(jugadores[jugador].getMano())){
			System.out.println("Es el turno del jugador "+jugador);
			if(jugador==0){
				while(!flag){ //repite hasta que seleccione una carta válida
					cartaNumero=seleccionarCarta();
					if(validarJugada(cartaNumero)){
						flag=true;
					}else{
						System.out.println("No puede jugar esa carta");
					}
				}
			}else{
				cartaNumero=generarJugada(jugador);
			}
			this.cartaNumeroPozo=cartaNumero;//
			this.mazo.setUso(cartaNumero);//
			this.jugadores[jugador].getMano().borrarCarta(this.jugadores[jugador].getMano().buscarCarta(cartaNumero));
			cantidadCartasRestantes--;
			System.out.println("Presione enter para continuar");
			teclado.nextLine();			
		}else{
			System.out.println("El jugador "+jugador+" no tiene carta para jugar, roba una carta");
			cartaNumero=generarCarta();
			robarCarta(jugador,cartaNumero);
			if(jugador==0)
				mostrarMano(jugador);
		}
		carta=this.mazo.getCarta(this.cartaNumeroPozo);
		//mostrarPozo(carta);		
	}
	// Método que escoge una carta de la mano de un jugador de forma aleatoria
	// retorna el número de la carta
	public int generarJugada(int jugador){
		boolean flag=false;
		int posicion;
		int cartaNumero;
		int largo=jugadores[jugador].getMano().largo();

		while(!flag){
			posicion = (int)(Math.random()*1000);
			posicion = posicion%largo;
			cartaNumero = jugadores[jugador].getMano().getNumeroCarta(posicion);
			if(validarJugada(cartaNumero)){
				flag=true;
				return cartaNumero;
			}
		}
		return -1;
	}

	// Método para mostrar la carta en el tope del pozo
	private void mostrarPozo(Carta carta){
		System.out.println("************************************");
		mostrarCarta(carta,0);
		System.out.println("************************************");
	}

	// Método para mostrar una mano de un jugador
	private void mostrarMano(int jugador){
		String texto;
		int largo;
		System.out.println("Tu mano es:");
		System.out.println("************************************");
		for(int i=0;i<jugadores[jugador].getMano().largo();i++){
			mostrarCarta(jugadores[jugador].getMano().getCarta(i),i+1);
		}
		System.out.println("************************************");
	}

	// Método para mostrar una carta en pantalla
	public void mostrarCarta(Carta carta, int numero){
		String texto;
		int largo;
		texto=numero+". "+carta.getValor()+"-"+carta.getColor();
		largo=texto.length();

			for(int j=0;j<largo+3;j++){
				if(j<4)
					System.out.print(" ");
				else
					System.out.print("-");
			}
			System.out.println("\n"+numero+". | "+carta.getValor()+"-"+carta.getColor()+" |");
			for(int j=0;j<largo+3;j++){
				if(j<4)
					System.out.print(" ");
				else
					System.out.print("-");
			}
			System.out.println("\n");
	}

	// Método para validar si una jugada es válida, se recibe la carta y
	// se revisa si tiene el mismo color, valor o si es carta especial
	// retorna verdadero si se cumple una o más condiciones
	public boolean validarJugada(int cartaNumeroJugada){
		boolean flag=false;
		if(!flag){

			if(mazo.getCarta(cartaNumeroJugada).getValor().compareTo(mazo.getCarta(this.cartaNumeroPozo).getValor())==0){
				//System.out.println("Jugada por valor");
				flag=true;
			}
			if(mazo.getCarta(cartaNumeroJugada).getColor().compareTo(mazo.getCarta(this.cartaNumeroPozo).getColor())==0){
				//System.out.println("Jugada por color");
				flag=true;
			}
			if(mazo.getCarta(cartaNumeroJugada).getColor().compareTo("Especial")==0){
				//System.out.println("Jugada por carta especial");
				flag=true;
			}
		}
		return flag;
	}
	// Método que valida si el jugador posee al menos una carta en su
	// mano para poder jugar, retorna verdadero si cumple
	private boolean validarMano(Mano mano){
		int largo = mano.largo();
		for(int i=0;i<largo;i++){
			if(validarJugada(mano.getNumeroCarta(i))){
				return true;
			}
		}
		return false;
	}
	// Método permite a un jugador no computador seleccionar la carta que desea
	// jugar, se retorna el número de la carta seleccionada de su mano
	private int seleccionarCarta(){
		Scanner teclado = new Scanner(System.in);
		String input;
		int carta;
		//jugador 0 es el jugador no computador
		mostrarMano(0);
		System.out.println("Seleccione la carta a jugar");
		System.out.println("Ingrese el número de la carta");
		input=teclado.nextLine();
		carta = Integer.parseInt(input);
		return jugadores[0].getMano().getNumeroCarta(carta-1);
	}
	// Método que asigna una carta a un jugador y la agrega a su mano
	private void robarCarta(int jugador,int cartaNumero){
		this.jugadores[jugador].getMano().agregarCarta(this.mazo.getCarta(cartaNumero),cartaNumero);
		this.mazo.setJugadorAsignado(cartaNumero,jugador);
	}
}