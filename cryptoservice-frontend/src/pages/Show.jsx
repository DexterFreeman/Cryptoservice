import React from "react";
import showStore from "../store/showStore";
import { useParams } from "react-router-dom";
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import { Header } from "../components/Header";
export const Show = () => {
  const store = showStore();
  const params = useParams();

  const handleClick = (e) => {
    e.preventDefault() 
    store.fetchPrediction(store.resData.data.symbol)
  }


  React.useEffect(() => {
    store.fetchData(params.id);

    return () => {
      store.reset() 
    }
  }, []);



  return (
    <div>
      <Header back></Header>

      {store.resData.data && <>
      <header className="show-header">
        <img src={store.resData.data.image.large} />
        <h2>{store.resData.data.name}</h2>
      </header>
      <div className="prediction__container width">
        <button onClick={handleClick}>Get prediction</button>
        {store.predictionData.data && <div className="prediction__container-values"><h2>Prediction in 7 days:</h2>
        <h3>{store.predictionData.data.predicted_price}</h3></div>}
      </div>
     
      
      <div className="width">
      <div className="show-graph">
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart
            data={store.graphData}
            margin={{
              top: 10,
              right: 30,
              left: 0,
              bottom: 0,
            }}
          >
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="Date" />
            <YAxis />
            <Tooltip />
            <Area
              type="monotone"
              dataKey="Price"
              stroke="#8884d8"
              fill="#8884d8"
            />
          </AreaChart>
        </ResponsiveContainer>
    
      </div>
      <div className="show-details">
        <h2>Details</h2>
      <div className="show-details-row">
        <h4>Market cap rank⠀</h4>
        <span>{store.resData.data.market_cap_rank}</span>
      </div>
      <div className="show-details-row">
        <h4>24H high⠀</h4>
        <span>${store.resData.data.market_data.high_24h.usd}</span>
      </div>
      <div className="show-details-row">
        <h4>24H low⠀</h4>
        <span>${store.resData.data.market_data.low_24h.usd}</span>
      </div>
      <div className="show-details-row">
        <h4>Circulating supply⠀</h4>
        <span>${store.resData.data.market_data.circulating_supply}</span>
      </div>
      <div className="show-details-row">
        <h4>Current price⠀</h4>
        <span>${store.resData.data.market_data.current_price.usd}</span>
      </div>
      <div className="show-details-row">
        <h4>1yh Change⠀</h4>
        <span>
          {store.resData.data.market_data.price_change_percentage_1y}%
        </span>
      </div>
      </div>
     
    </div> </>}
    </div>    
  );
};
