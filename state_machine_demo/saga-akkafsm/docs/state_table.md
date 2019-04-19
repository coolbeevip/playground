

<table style="text-align:center;"> 
 <caption>
  <b>Saga State Transition Table</b>
 </caption> 
 <tbody>
  <tr> 
   <th style="background:linear-gradient(to top right,#eaecf0 49.5%,#aaa 49.5%,#aaa 50.5%,#eaecf0 50.5%);line-height:1;border: 1px solid #a2a9b1;">
      <div style="margin-left:2em;text-align:right;">
       <small>State<br />(Next)</small>
      </div>
      <div style="margin-right:2em;text-align:left;">
       <small>State<br />(Current)</small>
      </div> 
    </th> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">IDEL</th> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">ACTIVE</th> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">PARTIALLY_COMMITTED</th> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">PARTIALLY_ACTIVE</th> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">FAILED</th>
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">COMPENSATED</th>   
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">COMMITTED</th>    
  </tr> 
  <tr> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">IDEL</th> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">E<sub>ss</sub></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">E<sub>ts</sub></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">E<sub>se</sub></td>    
  </tr> 
  <tr> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">ACTIVE</th> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">E<sub>te</sub></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">E<sub>ta</sub></td>    
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>    
  </tr> 
  <tr> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">PARTIALLY_COMMITTED</th> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">E<sub>ts</sub></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">E<sub>se</sub></td>    
  </tr> 
  <tr> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">PARTIALLY_ACTIVE</th> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">E<sub>te</sub></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">E<sub>ta</sub></td>    
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>    
  </tr>
  <tr> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">FAILED</th> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">IE<sub>sfc</sub></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>    
  </tr>  
  <tr> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">COMPENSATED</th> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>    
  </tr> 
  <tr> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">COMMITTED</th> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>    
  </tr>   
 </tbody>
</table>


<table style="text-align:center;"> 
 <caption>
  <b>Tx State Transition Table</b>
 </caption> 
 <tbody>
  <tr> 
   <th style="background:linear-gradient(to top right,#eaecf0 49.5%,#aaa 49.5%,#aaa 50.5%,#eaecf0 50.5%);line-height:1;border: 1px solid #a2a9b1;">
      <div style="margin-left:2em;text-align:right;">
       <small>State<br />(Next)</small>
      </div>
      <div style="margin-right:2em;text-align:left;">
       <small>State<br />(Current)</small>
      </div> 
    </th> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">IDEL</th> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">ACTIVE</th> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">FAILED</th>
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">COMMITTED</th>    
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">COMPENSATED</th>        
  </tr> 
  <tr> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">IDEL</th> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">E<sub>ts</sub></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>    
  </tr> 
  <tr> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">ACTIVE</th> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">E<sub>ta</sub></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">E<sub>te</sub></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>    
  </tr> 
  <tr> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">FAILED</th> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>    
  </tr>
  <tr> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">COMMITTED</th> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;">E<sub>co</sub></td>    
  </tr>    
  <tr> 
   <th style="background-color: #eaecf0;border: 1px solid #a2a9b1;">COMPENSATED</th> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td> 
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>
   <td style="background-color: #f8f9fa;border: 1px solid #a2a9b1;"></td>    
  </tr>     
 </tbody>
</table>

