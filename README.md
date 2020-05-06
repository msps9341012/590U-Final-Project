# Documentation
## Project Structure
### Android Side
> `/app folder`
* MenuActivity
    * Provide Menu UI
    * When open the app, upload the user's latest timeline (GPS records/the number of bluetooth device he met) to server and get the social distance score
* MyService
    * Record the user's GPS loaction and scan nearby bluetooth device every 20 minutes
    * Do in background, still working even the app is closed
* TimeLine(Activity/Adapter/ViewHolder)
    * For showing the user's timeline.
    * Use and modify the code in this [repo](https://github.com/vipulasri/Timeline-View)
* Leadboard
    * Connect to server and show social distance score leadboard in webview.
* MapActivity
    * Get COVID-19 cases by city/town and their corresponding longitude/latitude from server
    * Visualize the data on Google map using heatmap method (the more cases, the darker color) 
* JsonParser
    * Handle server connection issues
* Setting
    * User can turn on/off recording in background and change the recording interval here.
    
### Server Side
> `/server folder`
* Use python flask as server framework
* Will parse data from mass.gov and get the latest COVID-19 cases by city/town.
* Functions in t.py (server code)
    * calculate: get the user's timeline and calculate score
    * get_score: show leaderboard in html
        * I use open dataset as the score data here.
        * The dataset does not have any meaning, just for demo.  
    * get_data: return the cases data and each city boundaries (in longitude/latitude format)
* To run: `python t.py`

## Social Distance Score definition
* Value: 0~100(the most dangerous)
* score=the sum of (the number of people * risk level in that area)
    * Risk level: 1~4
        * Divide the cased data distribution into 4 parts evenly.
        * 1: lowest, 4: highest
    * the number of people: the number of bluetooth device 


# Report
* Your idea behind the app
    * Instead of manually recording GPS or scaning devices, my app can perform these functions in background automatically and periodically, which make the user's life easier.
    * Provide confirmed cases map, user can prevent himself from going to high-risk area.
    * Separating functions into server and app side makes my app highly extendable.
        * Changing scoring fuction or adding new features is easy.
    * Area issue is considered when calculating the social distance score.
        * If user visits an area with high risks, the people he met would be more dangerous, leading to higher score.
* UI/UX design:
    * Use progress bar to show score.
    * Show the user's location history in timeline view, just like what Google Map does.
    * Arrange menu button in GridLayout
    * Different warning message for different score
    ![](https://i.imgur.com/HTeXTJw.png)

* Differentiating your contribution
    * I modified the baseline code (only GPS/bluetooth part) into background service.
    * UI and other functions are created by myself.
