import axios from 'axios'
import { create } from 'zustand'
import React, { PureComponent } from 'react';


const showStore = create((set) => ({


    graphData: [], 
    resData: [], 

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
        console.log(dataRes)
        set({graphData: graphData})
        set({resData: dataRes})
    },


}))




export default showStore