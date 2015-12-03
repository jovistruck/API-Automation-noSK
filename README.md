# End to end NowTv 2.0 functional test project

### Running from gradle:
    
    gradle -Dtag=@MYTESTTAG test
    
    gradlew test -Dtags=@BOBillingBillDetails -DPO_URL=po_url -DSO_URL=so_url -DBO_URL=bo_url
    OR
    gradlew test -Dtags=@BOBillingBillDetails -Denv=development
    
### Ignored tags
    gradlew test -Dtags=@BOBillingBillDetails -Denv=development
