import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Entities.*;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

public class TShirtsDB4O {
	public static ArrayList<Order> orderList;
	static ObjectContainer db;

	public static void main(String[] args) throws IOException, ParseException {
		TShirtsDB4O TSM = new TShirtsDB4O();
		FileAccessor fileAccessor = new FileAccessor();
		fileAccessor.readArticlesFile("articles.csv");
		fileAccessor.readCreditCardsFile("creditCards.csv");
		fileAccessor.readCustomersFile("customers.csv");
		fileAccessor.readOrdersFile("orders.csv");
		fileAccessor.readOrderDetailsFile("orderDetails.csv");
		orderList = fileAccessor.orders;
		try {
			File file = new File("orders.db");
			String dbPath = file.getAbsolutePath();
			db = Db4o.openFile(dbPath);

			TSM.addOrders();
			TSM.listOrders();
			TSM.listArticles();
			TSM.addArticle(7, "CALCETINES EJECUTIVOS 'JACKSON 3PK'", "gris", "45", 18.00);
			TSM.updatePriceArticle(7, 12.00);
			TSM.llistaArticlesByName("CALCETINES EJECUTIVOS 'JACKSON 3PK'");
			TSM.deletingArticlesByName("POLO BÁSICO 'MANIA'");
			TSM.deleteArticleById(7);
			TSM.listArticles();
			TSM.listCustomers();
			TSM.changeCreditCardToCustomer(1);
			TSM.listCustomers();
			TSM.llistaCustomerByName("Laura");
			TSM.showOrdersByCustomerName("Laura");
			TSM.showCreditCardByCustomerName("Laura");
			TSM.deleteCustomerbyId(2);
			TSM.retrieveOrderContentById_Order(2);
			TSM.deleteOrderContentById_Order(2);
			TSM.retrieveOrderContentById_Order(2);
			TSM.listCustomers();
			TSM.clearDatabase();
			TSM.listOrders();
		} finally {
			db.close();
		}
	}

	public void changeCreditCardToCustomer(int customerId) {
		System.out.println("Poner una nueva tarjeta de crédito a un cliente");
		int[] creditDigits = new int[16];
		Random random = new Random();
		for (int j = 0; j < 16; j++) {
			creditDigits[j] = random.nextInt(10);
		}
		String creditNumber = "";
		for (int digit : creditDigits) {
			creditNumber += digit;
		}
		CreditCard creditCard = new CreditCard(creditNumber, "" + creditDigits[2] + creditDigits[5] + creditDigits[12],
				random.nextInt(12) + 1, random.nextInt(10) + 20);
		db.store(creditCard);
		ObjectSet<Customer> result = db.queryByExample(new Customer(customerId, null, null, null, null, null));
		if (result.hasNext()) {
			Customer customer = result.next();
			customer.setCreditCard(creditCard);
			db.store(customer);
		}
	}

	public void updatePriceArticle(int articleId, double newPrice) {
		System.out.println("Actualizar precio de artículo");
		Article article = new Article();
		article.setIdArticle(articleId);
		ObjectSet<Article> result = db.queryByExample(article);
		if (result.hasNext()) {
			Article foundArticle = result.next();
			foundArticle.setRecommendedPrice((float) newPrice);
			db.store(foundArticle);
		}
	}

	public void addArticle(int articleId, String name, String color, String size, double price) {
		System.out.println("Añadir artículo");
		Article article = new Article(articleId, name, color, size, (float) price);
		db.store(article);
		System.out.println(article.toString());
	}

	public void deleteArticleById(int articleId) {
		System.out.println("Borrar artículo por ID");
		Article article = new Article();
		article.setIdArticle(articleId);
		ObjectSet<Article> result = db.queryByExample(article);
		while (result.hasNext()) {
			db.delete(result.next());
		}
	}

	public void deleteOrderContentById_Order(int orderId) {
		System.out.println("Borrar contenido del pedido por ID de pedido");
		Order order = new Order();
		order.setIdOrder(orderId);
		ObjectSet<Order> result = db.queryByExample(order);
		if (result.hasNext()) {
			Order foundOrder = result.next();
			foundOrder.setDetails(null);
			db.store(foundOrder);
		}
	}

	public void retrieveOrderContentById_Order(int orderId) {
		System.out.println("Recuperar contenido del pedido por ID de pedido");
	}

	public void deleteCustomerbyId(int customerId) {
		System.out.println("Borrar cliente por ID");
		ObjectSet<Customer> result = db.queryByExample(new Customer(customerId, null, null, null, null, null));
		while (result.hasNext()) {
			db.delete(result.next());
		}
	}

	public void showCreditCardByCustomerName(String customerName) {
		List<Customer> customers = db.query(new Predicate<Customer>() {
			public boolean match(Customer customer) {
				return customer.getName().equalsIgnoreCase(customerName);
			}
		});
		System.out.println("Mostrar tarjeta de crédito por nombre del cliente");
		for (Customer customer : customers) {
			System.out.println(customer.getCreditCard());
		}

	}

	public void showOrdersByCustomerName(String customerName) {
		List<Order> orders = db.query(new Predicate<Order>() {
			public boolean match(Order order) {
				return order.getCustomer().getName().equalsIgnoreCase(customerName);
			}
		});
		System.out.println("Mostrar pedidos por nombre del cliente");
		for (Order order : orders) {
			System.out.println(order.toString());
		}
	}

	public void clearDatabase() {
		ObjectSet<Object> result = db.queryByExample(Object.class);
		while (result.hasNext()) {
			db.delete(result.next());
		}
		System.out.println("base de datos limpiada");
	}

	public void deletingArticlesByName(String articleName) {
		System.out.println("Borrar artículos por nombre");
		Article article = new Article();
		article.setName(articleName);
		ObjectSet<Article> result = db.queryByExample(article);
		while (result.hasNext()) {
			db.delete(result.next());
		}
	}

	public void llistaArticlesByName(String articleName) {
		System.out.println("listar article con nombre");
		ObjectSet<Article> result = db.query(new Predicate<Article>() {
			@Override
			public boolean match(Article article) {
				return article.getName().equalsIgnoreCase(articleName);
			}
		});
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}

	public void llistaCustomerByName(String customerName) {
		System.out.println("listar customer con nombre");
		ObjectSet<Customer> result = db.query(new Predicate<Customer>() {
			@Override
			public boolean match(Customer customer) {
				return customer.getName().equalsIgnoreCase(customerName);
			}
		});
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}

	public void listCustomers() {
		System.out.println("Listar todos los clientes");
		ObjectSet<Customer> result = db.queryByExample(new Customer());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}

	public void listArticles() {
		System.out.println("Listar todos los artículos");
		ObjectSet<Article> result = db.queryByExample(new Article());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}

	public void addOrders() {
		System.out.println("Añadir pedidos");
		for (Order order : orderList) {
			db.store(order);
			System.out.println(order.toString());
		}
	}

	public void listOrders() {
		System.out.println("Listar todos los pedidos");
		ObjectSet<Order> result = db.queryByExample(new Order());
		while (result.hasNext()) {
			System.out.println(result.next());
		}
	}
}
