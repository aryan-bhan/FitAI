import React, { useEffect, useState} from 'react'
import { getActivityDetail } from '../services/api';
import { Typography,Box,Card,CardContent,Divider } from '@mui/material';
import { useParams } from 'react-router';



const ActivityDetail = () => {
  const params = useParams();
  const [activity,setActivity] = useState(null);
  const [recommendation,setRecommendation] = useState(null);
  
  useEffect(()=>{
    fetchActivityDetail();
  },[params]);

  const fetchActivityDetail = async ()=>{
      try{
        const res = await getActivityDetail(params.id);
        setActivity(res.data);
        setRecommendation(res.data.recommendation);
      }catch(e){
        console.error(e);
      }
    }

  if(!activity){
    return <Typography>Loading...</Typography>
  }
  return (
     <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>
            <Card sx={{ mb: 2 }}>
                <CardContent>
                    <Typography variant="h5" gutterBottom>Activity Details</Typography>
                    <Typography>Type: {activity.type}</Typography>
                    <Typography>Duration: {activity.duration} minutes</Typography>
                    <Typography>Calories Burned: {activity.caloriesBurned}</Typography>
                    <Typography>Date: {new Date(activity.createdAt).toLocaleString()}</Typography>
                </CardContent>
            </Card>

            {recommendation && (
                <Card>
                    <CardContent>
                        <Typography variant="h5" gutterBottom>AI Recommendation</Typography>
                        <Typography variant="h6">Analysis</Typography>
                        <Typography component = "p">{activity.recommendation}</Typography>
                        
                        <Divider sx={{ my: 2 }} />
                        
                        <Typography variant="h6">Improvements</Typography>
                        {activity?.improvements?.map((improvement, index) => (
                            <Typography key={index} component = "p">• {improvement}</Typography>
                        ))}
                        
                        <Divider sx={{ my: 2 }} />
                        
                        <Typography variant="h6">Suggestions</Typography>
                        {activity?.suggestions?.map((suggestion, index) => (
                            <Typography key={index} component = "p">• {suggestion}</Typography>
                        ))}
                        
                        <Divider sx={{ my: 2 }} />
                        
                        <Typography variant="h6">Safety Guidelines</Typography>
                        {activity?.safety?.map((safety, index) => (
                            <Typography key={index} component = "p">• {safety}</Typography>
                        ))}
                    </CardContent>
                </Card>
            )}
        </Box>
  )
}

export default ActivityDetail