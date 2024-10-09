import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

import org.postgresql.Driver;

public class Main
{
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);

        dao_postgres driverManager = new dao_postgres();

        String user = "postgres";
        String pwd  = ""; //insert_your_password

        driverManager.setURL("localhost","postgres", 5432);
        driverManager.Connect(user, pwd);

        Connection connection = driverManager.getConnection();

        while (true)
        {
            System.out.print("1. Добавить книгу\n2. Обновить данные о книге\n3. Удалить книгу" +
                    "\n4. Вывести информацию о книгах\n5. Выйти\n");

            System.out.print("Введите номер пункта: ");
            char punkt = in.next().charAt(0);
            in.nextLine();

            System.out.println();
            switch (punkt)
            {
                case '1':
                {
                    System.out.println("\t-Добавление книги-");
                    System.out.print("Введите название книги: "); String title = in.nextLine();
                    System.out.print("Введите автора книги: "); String author = in.nextLine();
                    System.out.print("Введите год издания книги: "); int year = in.nextInt();
                    in.nextLine();

                    addBook(driverManager, title, author, year);

                    System.out.println();
                    break;
                }
                case '2':
                {
                    System.out.println("\t-Обновление данных о книге-");
                    System.out.print("Введите id книги: "); int id = in.nextInt();
                    in.nextLine();

                    try
                    {
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(
                                "SELECT * FROM public.\"Books\" ORDER BY id ASC ");

                        boolean flag = false;
                        while(resultSet.next())
                        {
                            int resId = resultSet.getInt("id");

                            if(resId == id)
                            {
                                flag = true;
                                break;
                            }
                        }

                        if(!flag)
                        {
                            System.out.println("Книга с данным id не найдена.");
                            break;
                        }

                        resultSet.close();
                        statement.close();
                    }
                    catch (SQLException e)
                    {
                        throw new RuntimeException(e);
                    }

                    System.out.print("Введите название книги: "); String title = in.nextLine();
                    System.out.print("Введите автора книги: "); String author = in.nextLine();
                    System.out.print("Введите год издания книги: "); int year = in.nextInt();
                    in.nextLine();

                    updateBook(driverManager, id, title, author, year);

                    System.out.println();
                    break;
                }
                case '3':
                {
                    System.out.println("\t-Удаление книги-");
                    System.out.print("Введите id книги: "); int id = in.nextInt();

                    deleteBook(driverManager, id);

                    System.out.println();
                    break;
                }
                case '4':
                {
                    System.out.println("\t-Информация о книгах-");
                    printBooksInfo(connection);
                    break;
                }
                case '5':
                {
                    return;
                }
                default:
                    break;
            }
        }
    }

    public static void printBooksInfo(Connection connection)
    {
        try
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM public.\"Books\" ORDER BY id ASC ");

            while(resultSet.next())
            {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int year = resultSet.getInt("year");

                System.out.printf("id: %d\ntitle: %s\nauthor: %s\nyear: %d\n\n", id, title, author, year);
            }

            resultSet.close();
            statement.close();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void addBook(dao_postgres driverManager, String title, String author, int year)
    {
        String query = """
                INSERT INTO public."Books"
                (title, author, year)
                VALUES ('%s', '%s', %d)
            """;
        driverManager.execSQL(String.format(query, title, author, year));
    }

    public static void updateBook(dao_postgres driverManager, int id, String title, String author, int year)
    {
        String query = """
                UPDATE public."Books"
                SET title = '%s',
                    author = '%s',
                    year = %d
                WHERE id = %d;
            """;
        driverManager.execSQL(String.format(query, title, author, year, id));
    }

    public static void deleteBook(dao_postgres driverManager, int id)
    {
        String query = """
                DELETE FROM public."Books"
                WHERE id = %d;
            """;
        driverManager.execSQL(String.format(query, id));
    }
}