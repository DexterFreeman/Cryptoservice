import axios from 'axios'
import { create } from 'zustand'
import React, { PureComponent } from 'react';


const showStore = create((set) => ({


    graphData: [], 
    resData: [], 
    predictionData: [], 

    reset: () => {
        set({graphData: [], data: null, resData: []})
    },

    fetchPrediction: async (symbol) => {
        
        symbol = symbol.toUpperCase() 
        try {
            const res = await axios.get(`http://127.0.0.1:8000/predictions/get/?currency=${symbol}`);
            set({predictionData: res})
        }
        catch(error){
            console.error('Error fetching prediction:', error); 
        }
        

    },

    fetchData: async (id) => {
    
        const [graphRes, dataRes] = await Promise.all([
            axios.get(`https://api.coingecko.com/api/v3/coins/${id}/market_chart?vs_currency=usd&days=24`),
            axios.get(`https://api.coingecko.com/api/v3/coins/${id}?localization=false&market_data=true`),
        ])
      
        
  
        const graphData = graphRes.data.prices.map((price) => {
            const [timestamp, p] = price; 
            const date = new Date(timestamp).toLocaleDateString('en-us')
            return {
                Date: date, 
                Price: p, 


            }
        })
        
        set({graphData: graphData})
        set({resData: dataRes})
        
    },


}))




export default showStore