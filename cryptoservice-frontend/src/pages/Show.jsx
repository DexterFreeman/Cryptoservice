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
import Button from "../components/Button";
import DetailsGrid from "../components/DetailsGrid";
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
    <div className="show-page">
      <Header back></Header>
      {store.resData.data && <>
      <header className="show-header">
        <img src={store.resData.data.image.large} />
        <h2>{store.resData.data.name}</h2>
      </header>
      
      <div className="prediction__container width">
        <Button handleClick={handleClick} innerText={"Get Prediction"}></Button>
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
      
    </div> </>}
    {store.resData.data && 
    <div className="width"><DetailsGrid  market_data={store.resData.data.market_data}></DetailsGrid></div>
    }
    </div>    
  );
};
