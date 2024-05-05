package lambda;

import org.jsoup.Jsoup;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Lambda {
    public static void main(String[] args) throws Exception{

        String urlBase = "https://www.pogoda.spb.ru/";
        var document = Jsoup.connect(urlBase).get();


        // beginning of selection
        String table = "body > div > div > table > tbody > tr:nth-child(2) > td:nth-child(1) > table.wt > tbody "; // "> tr:nth-child(1)"
        StringBuilder[] tables = new StringBuilder[5];

        List<String[]> weather = new ArrayList<>();
        String tableNChild;

        StringBuilder stringWeather = new StringBuilder();
        String weatherChild;
        for (int i = 1; i <= 27; i++)
        {
            tableNChild = table + "> tr:nth-child(" + i + ")";

            if (i == 1 || i == 7 || i == 12 || i == 17 || i == 23)
            {
                tableNChild = tableNChild + " > #dt";
                tables[i/5] = new StringBuilder(document.select(tableNChild).text());
            }

            else if (i <= 6 && i >= 3 || i <= 11 && i >= 8 || i <= 16 && i >= 13 || i <= 21 && i >= 18 || i >= 24){
                stringWeather.delete(0, stringWeather.length());
                for (int j = 1; j<=3; j++)
                {

                    weatherChild = tableNChild + "> td:nth-child(" + j + ")";
                    stringWeather.append(document.select(weatherChild).text()).append(',');
                }
                weather.add(stringWeather.toString().split(","));
            }
        }
        // okey, dates were parsed

        for (StringBuilder builder: tables){
            int countProbels = 0;
            for (int i = 6; i < builder.length(); i++){
                if (builder.charAt(i) == ' ')
                {

                    countProbels++;
                    if (countProbels > 0)
                    {
                        builder.delete(i, builder.length());
                    }
                }

            }
        }

//        for (StringBuilder stringBuilder: tables){
//            System.out.println(stringBuilder);
//        }
//
//        for (String[] weath: weather){
//            Arrays.stream(weath).forEach(n -> System.out.print(n + "_"));
//            System.out.println();
//        }
        // ok,


        String url = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "17488471";

        String sql = """
                    create table if not exists weather(
                    date_day text not null,
                    time text not null,
                    comma text not null,
                    temp text not null
                );""";


        StringBuilder sql1 = new StringBuilder("""
                insert into weather values
                """);

        String[] string;
        for (int i = 0; i < 20; i++){
            string = weather.get(i);
            sql1.append("('" + tables[i/4] + "', '" + string[0] + "', '" + string[1] + "', '" + string[2] + "')");
            if (i != 19){
                sql1.append(",");
                sql1.append("\n");
            }
            else sql1.append(";");
        }

        //System.out.println(sql1);

        try (Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement())
       {
//           System.out.println(sql2);
//            var res2 = statement.execute(sql2);

           System.out.println(sql);
            var res = statement.execute(sql);

           System.out.println(sql1);
            var res1 = statement.execute(String.valueOf(sql1));


        } catch (SQLException e){
            System.out.println("could not set connection");
        }

    }

}
