# GentriFind
 By definition, gentrification is a process of changing the character of a neighborhood through the influx of more affluent residents and businesses so that the neighborhood conforms to middle-class tastes. Gentrification often shifts a neighborhood's racial or ethnic composition, and increases the economic value of a neighborhood, and average household income by developing new, more expensive housing, businesses and improved resources. 
 
In May 2018, the American Economic Association published “[Nowcasting Gentrification: Using Yelp Data to Quantify Neighborhood Change](https://www.aeaweb.org/articles?id=10.1257/pandp.20181034)”,  which explored the correlation between the number of Starbucks, and housing price growth in a neighborhood. The study concluded that local economic activity (such as the growing number of cafes) , as measured by Yelp data, is a leading indicator for housing price changes and can help to predict which neighborhoods are gentrifying. 

## Description
Taking inspiration from the study, GentriFind uses data on the number of Starbucks in an area and current housing prices to calculate the increase in housing prices, and determine whether a neighborhood is in danger of gentrification. 
 
### How it Works: 
1. When you open the app, you will be greeted with the HomeScreen, which displays the question “Is Your Neighborhood in Danger of Gentrification?” and a “Go” button. 
2. Upon clicking the “Go” button, the app will access your location.
3. The app will search Yelp for all Starbucks within X miles of your location.
4. The app will access Zillow and use your zipcode to find the median listing price for properties in your neighborhood. 
5. The app will calculate the predicted new price due to gentrification by inflating the current median price by 0.5% for every Starbucks found.
6. A new screen will display the current median listing price, as well as the adjusted price due to expected gentrification. 

## Next Steps: 

## Screenshots
![Home Screen](screenshots/HomeScreen.png)
[Link to the paper](https://www.aeaweb.org/articles?id=10.1257/pandp.20181034)
