package logica.componentes.propriedades;

public class FabricaPropriedades {
	public static Propriedade[] cria() {
		
		int[] preco_compra = { // cada linha simboliza uma cor (empresas entram no meio e fim de cada cor)
				100, 60, 60, 200,
				240, 200, 220, 220,
				220, 310, 310, 150,
				350, 400,
				120, 100, 150,
				160, 140, 140,
				260, 200, 320, 300, 200, 300,
				280, 260
		};
		
		int[][] aluguel = { // cada linha simboliza uma cor (empresas tem apenas 1 preco)
				{6,30,90,270,400,500}, {2,10,30,90,160,250}, {4,20,60,180,320,450}, {50},
				{20,100,300,750,925,1100}, {50}, {18,90,250,700,875,1050}, {18,90,250,700,875,1050},
				{16,80,220,600,800,1000}, {14,70,200,550,750,950}, {14,70,200,550,750,950}, {40},
				{35,175,500,1100,1300,1500}, {50,200,600,1400,1700,2000},
				{8,40,100,300,450,600}, {6,30,90,270,400,500}, {40},
				{12,60,180,500,700,900}, {10,50,150,450,625,750}, {10,50,150,450,625,750},
				{22,110,330,800,975,1150}, {50}, {28,150,450,1000,1200,1400}, {26,130,390,900,1100,1275}, {50}, {26,130,390,900,1100,1275},
				{24,120,360,850,1025,1200}, {22,110,330,800,975,1150}
		};
		
		String[] nomes = {
				"Leblon", "Av. Presidente Vargas", "Av. Nossa S. de Copacabana", "Companhia Ferroviaria",
				"Av. Brig. Faria Lima", "Companhia de Onibus", "Av. Rebouças", "Av. 9 de Julho",
				"Av. Europa", "Rua Augusta", "Av. Pacaembu", "Companhia de Taxi",
				"Interlagos", "Morumbi",
				"Flamengo", "Botafogo", "Companhia de Navegacao",
				"Av. Brasil", "Av. Paulista", "Jardim Europa",
				"Copacabana", "Companhia de Aviacao", "Av. Vieira Souto", "Av. Atlantica", "Companhia de Helicoptero", "Ipanema",
				"Jardim Paulista", "Brooklin"
			};
		
		Propriedade[] prop = new Propriedade[28];
		
		for(int i = 0; i < 28; i++) {
			if (i == 3 || i == 5 || i == 11 || i == 16 || i == 21 || i == 24)
				prop[i] = new Empresa(preco_compra[i], aluguel[i], nomes[i]);
			else {
				if(i < 3)
					prop[i] = new Terreno(preco_compra[i], aluguel[i], nomes[i], "Rosa");
				else if(i < 8)
					prop[i] = new Terreno(preco_compra[i], aluguel[i], nomes[i], "Azul");
				else if(i < 11)
					prop[i] = new Terreno(preco_compra[i], aluguel[i], nomes[i], "Violeta");
				else if(i < 14)
					prop[i] = new Terreno(preco_compra[i], aluguel[i], nomes[i], "Laranja");
				else if(i < 16)
					prop[i] = new Terreno(preco_compra[i], aluguel[i], nomes[i], "Vermelho");
				else if(i < 20)
					prop[i] = new Terreno(preco_compra[i], aluguel[i], nomes[i], "Amarelo");
				else if(i < 25)
					prop[i] = new Terreno(preco_compra[i], aluguel[i], nomes[i], "Verde");
				else
					prop[i] = new Terreno(preco_compra[i], aluguel[i], nomes[i], "Roxo");
			}
		}
			
		
		return prop;
	} 
}
