# Sparrow

##### A repository to play around with AWS Lambda.

## Local development setup Do an `npm sintall`.
- Ensure that the `api.gridr.io` and `app.gridr.io` buckets exist in AWS S3.
- Build the uberjar with `lein clean && lein uberjar`.
- Deploy the backend stack with `serverless deploy`
- Tear it all down with `serverless client remove` and `serverless remove`.
