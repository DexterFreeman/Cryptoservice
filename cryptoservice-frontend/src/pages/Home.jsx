import React from 'react'
import homeStore from '../store/homeStore'
import { Link } from 'react-router-dom'

export const Home = () => {
    const store = homeStore()

    React.useEffect(() => {
        store.fetchCoins() 
    }, [])
  return (
    <div >
        <input type="text" name="" id="" value={store.query} onChange={(e) => {store.setQuery(e)}} />
        {store.coins.map((coin) => {
        return (
            <div key={coin.id}>
                <Link to={`/${coin.id}`}>{coin.name}</Link>

            </div>
        )
        })}
    </div>
  )
}
